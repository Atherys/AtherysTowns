package com.atherys.towns.managers;

import com.atherys.towns.AtherysTowns;
import com.atherys.towns.Settings;
import com.atherys.towns.db.TownsDatabase;
import com.atherys.towns.plot.Plot;
import com.atherys.towns.utils.DbUtils;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.result.DeleteResult;
import org.bson.Document;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.world.BlockChangeFlag;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

public final class WildernessManager {

    private static WildernessManager instance = new WildernessManager();

    private Random random = new Random();
    private Map<String,Map<String,Double>> filter = new HashMap<>();

    private WildernessManager() {
        Settings.WILDERNESS_REGEN_FILTER.entrySet().forEach( (entry) -> {
            Map<String,Double> alts = new HashMap<>();
            entry.getValue().getAsJsonObject().entrySet().forEach( (alt) -> alts.put( alt.getKey(), alt.getValue().getAsDouble() ));
            filter.put(entry.getKey(), alts);
        });

        collection().createIndex(new Document("location", 1), new IndexOptions().unique(true));
    }

    private MongoCollection<Document> collection() {
        return TownsDatabase.getInstance().getDatabase().getCollection("wilderness");
    }

    public void saveOne ( Transaction<BlockSnapshot> transaction ) {
        // if original is air, a block was placed
        // if original is not air, a block was destroyed

        BlockSnapshot original = transaction.getOriginal();
        BlockType originalBlock = original.getExtendedState().getType();

        if ( !( filter.containsKey(originalBlock.getName()) || filter.containsKey(transaction.getFinal().getState().getType().getName()) ) ) return;

        if ( originalBlock.equals(BlockTypes.AIR) ) {
            // a block was placed
            // store original snapshot into database, so when server restores blocks, it will restore it back to air
            insertOne(transaction.getOriginal());
        } else {
            // a block was destroyed
            // run filter, determine block to store, use placed block
            BlockState finalState = original.getState();

            Map<String,Double> v = filter.get(originalBlock.getName());
            if ( v != null ) {
                float r = random.nextFloat();

                for ( Map.Entry<String,Double> entry : v.entrySet() ) {

                    if ( r <= entry.getValue() ) {

                        Optional<BlockType> regenBlock = Sponge.getRegistry().getType(BlockType.class, entry.getKey());

                        if ( regenBlock.isPresent() ) {
                            // alt is valid, change snapshot type to alt

                            if ( regenBlock.get().equals(finalState.getType()) ) continue;
                            finalState = BlockState.builder().blockType(regenBlock.get()).build();

                        } else {
                            // alt is invalid

                            AtherysTowns.getInstance().getLogger().error( "[Wilderness] " + entry.getValue() + " is not a valid BlockType.");
                        }
                    }
                }

                BlockSnapshot regenSnap =
                        BlockSnapshot.builder()
                        .world(Sponge.getServer().getWorldProperties(original.getWorldUniqueId()).get())
                        .position(original.getPosition())
                        .blockState(finalState)
                        .build();

                insertOne(regenSnap);
            }
        }
    }

    public void regenerate ( long timestamp ) {
        MongoCursor<Document> cursor = collection().find(Filters.lte("timestamp", timestamp)).iterator();

        int restored = 0;
        while (cursor.hasNext()) {
            Document doc = cursor.next();
            BlockSnapshot snapshot = DbUtils.Deserialize.snapshot(doc.getString("snapshot"));

            Optional<Plot> plot = PlotManager.getInstance().getByLocation(snapshot.getLocation().get());
            if ( plot.isPresent() ) return;

            snapshot.restore(true, BlockChangeFlag.NONE);

        }

        DeleteResult deleteResult = collection().deleteMany(Filters.lte("timestamp", timestamp));
        AtherysTowns.getInstance().getLogger().info( "[Regeneration] " + restored + " snapshot(s) restored, and " + deleteResult.getDeletedCount() + " snapshot(s) deleted.");
    }

    private void insertOne ( BlockSnapshot snapshot ) {

        Optional<Location<World>> locOpt = snapshot.getLocation();
        if ( !locOpt.isPresent() ) return;

        Document location = DbUtils.Serialize.location(locOpt.get());

        // if there is already a block stored for that location, ignore the new one.
        if ( collection().count(new Document("location", location)) >= 1 ) return;

        //AtherysTowns.getInstance().getLogger().info("Inserting " + snapshot.toString());

        Document doc = new Document();
        doc.append("location", location);
        doc.append("snapshot", DbUtils.Serialize.snapshot(snapshot));
        doc.append("timestamp", System.currentTimeMillis());

        collection().insertOne( doc );
    }

    public static WildernessManager getInstance() {
        return instance;
    }

}
