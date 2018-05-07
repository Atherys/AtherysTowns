package com.atherys.towns.managers;

import com.atherys.towns.AtherysTowns;
import com.atherys.towns.db.TownsDatabase;
import com.atherys.towns.plot.Plot;
import com.atherys.towns.utils.DbUtils;
import com.atherys.towns.utils.WildernessFilter;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.result.DeleteResult;
import org.bson.Document;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.world.BlockChangeFlags;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

public final class WildernessManager {

    private static WildernessManager instance = new WildernessManager();

    private WildernessFilter filter;

    private Task wildernessRegenTask;

    private WildernessManager() {
    }

    public static WildernessManager getInstance() {
        return instance;
    }

    public void init() {

        if (AtherysTowns.getConfig().WILDERNESS_REGEN.ENABLED) {

            this.filter = AtherysTowns.getConfig().WILDERNESS_REGEN.FILTER;
            collection().createIndex(new Document("location", 1), new IndexOptions().unique(true));

            long delay = AtherysTowns.getConfig().WILDERNESS_REGEN.RATE;

            if (AtherysTowns.getConfig().WILDERNESS_REGEN.LAST != 0) {
                long elapsed =
                        System.currentTimeMillis() - AtherysTowns.getConfig().WILDERNESS_REGEN.LAST;
                delay = AtherysTowns.getConfig().WILDERNESS_REGEN.UNIT
                        .toMillis(AtherysTowns.getConfig().WILDERNESS_REGEN.RATE) - elapsed;
            }

            wildernessRegenTask = Task.builder()
                    .delay(AtherysTowns.getConfig().WILDERNESS_REGEN.UNIT
                                    .convert(delay < 0 ? 0 : delay, TimeUnit.MILLISECONDS),
                            AtherysTowns.getConfig().WILDERNESS_REGEN.UNIT)
                    .interval(AtherysTowns.getConfig().WILDERNESS_REGEN.RATE,
                            AtherysTowns.getConfig().WILDERNESS_REGEN.UNIT)
                    .execute(
                            () -> WildernessManager.getInstance().regenerate(System.currentTimeMillis()))
                    .name("atherystowns-wilderness-regen-task")
                    .submit(AtherysTowns.getInstance());
        }
    }

    private MongoCollection<Document> collection() {
        return TownsDatabase.getInstance().getDatabase().getCollection("wilderness");
    }

    public void saveOne(Transaction<BlockSnapshot> transaction) {
        if (!AtherysTowns.getConfig().WILDERNESS_REGEN.ENABLED) {
            return;
        }

        // if original is air, a block was placed
        // if original is not air, a block was destroyed

        BlockSnapshot original = transaction.getOriginal();
        BlockType originalBlock = original.getExtendedState().getType();

        if (!filter.has(originalBlock) && !filter
                .has(transaction.getFinal().getState().getType())) {
            return;
        }

        if (originalBlock.equals(BlockTypes.AIR)) {
            // a block was placed
            // store original snapshot into database, so when server restores blocks, it will restore it back to air
            insertOne(transaction.getOriginal());
        } else {
            insertOne(filter.getAlternatives(originalBlock.getName()).getFinal(original));
        }
    }

    public void regenerate(long timestamp) {
        MongoCursor<Document> cursor = collection().find(Filters.lte("timestamp", timestamp))
                .iterator();

        int restored = 0;
        while (cursor.hasNext()) {
            Document doc = cursor.next();
            BlockSnapshot snapshot = DbUtils.Deserialize.snapshot(doc.getString("snapshot"));

            Optional<Plot> plot = PlotManager.getInstance()
                    .getByLocation(snapshot.getLocation().get());
            if (plot.isPresent()) {
                return;
            }

            snapshot.restore(true, BlockChangeFlags.NONE);

        }

        DeleteResult deleteResult = collection().deleteMany(Filters.lte("timestamp", timestamp));
        AtherysTowns.getInstance().getLogger().info(
                "[Regeneration] " + restored + " snapshot(s) restored, and " + deleteResult
                        .getDeletedCount() + " snapshot(s) deleted.");

        AtherysTowns.getConfig().WILDERNESS_REGEN.LAST = timestamp;
        AtherysTowns.getConfig().save();
    }

    private void insertOne(BlockSnapshot snapshot) {

        Optional<Location<World>> locOpt = snapshot.getLocation();
        if (!locOpt.isPresent()) {
            return;
        }

        Document location = DbUtils.Serialize.location(locOpt.get());

        // if there is already a block stored for that location, ignore the new one.
        if (collection().count(new Document("location", location)) >= 1) {
            return;
        }

        //AtherysTowns.getInstance().getLogger().info("Inserting " + snapshot.toString());

        Document doc = new Document();
        doc.append("location", location);
        doc.append("snapshot", DbUtils.Serialize.snapshot(snapshot));
        doc.append("timestamp", System.currentTimeMillis());

        collection().insertOne(doc);
    }

}
