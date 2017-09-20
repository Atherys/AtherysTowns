package com.atherys.towns.managers;

import com.atherys.towns.AtherysTowns;
import com.atherys.towns.Settings;
import com.atherys.towns.db.TownsDatabase;
import com.google.gson.Gson;
import com.mongodb.client.MongoCollection;
import ninja.leaping.configurate.ConfigurationNode;
import org.bson.Document;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.item.ItemTypes;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

public final class WildernessManager {

    private static WildernessManager instance = new WildernessManager();

    private static Gson gson = new Gson();

    private Random random = new Random();
    private Map<String,Map<String,Double>> filter = new HashMap<>();

    private WildernessManager() {
        ConfigurationNode filterRoot = Settings.getInstance().getRoot().getNode("wilderness", "regenFilter");
        for ( ConfigurationNode filterNode : filterRoot.getChildrenList() ) {
            Map<String,Double> alts = new HashMap<>();
            filterNode.getChildrenList().forEach((n) -> alts.put((String) n.getKey(), n.getDouble()));
            filter.put((String) filterNode.getKey(), alts);
        }
    }

    private MongoCollection<Document> collection() {
        return TownsDatabase.getInstance().getDatabase().getCollection("wilderness");
    }

    public void saveOne ( Transaction<BlockSnapshot> transaction ) {
        // if original is air, a block was placed
        // if original is not air, a block was destroyed

        if ( transaction.getOriginal().getState().getType().equals(ItemTypes.AIR) ) {
            // a block was placed
            // store original snapshot into database, so when server restores blocks, it will restore it back to air
            insertOne(transaction.getOriginal());
        } else {
            // a block was destroyed
            // run filter, determine block to store
            BlockSnapshot original = transaction.getOriginal();
            BlockType placedBlock = original.getExtendedState().getType();
            filter.forEach( (k,v) -> {
                if ( k.equals(placedBlock.getName()) ) {
                    float r = random.nextFloat();

                    for ( Map.Entry<String,Double> entry : v.entrySet() ) {
                        if ( r <= entry.getValue() ) {
                            Optional<BlockType> regenBlock = Sponge.getRegistry().getType(BlockType.class, entry.getKey());
                            if ( regenBlock.isPresent() ) {
                                // alt is valid, change snapshot type to alt
                                insertOne( BlockSnapshot.builder().from(original).blockState(BlockState.builder().blockType(regenBlock.get()).build()).build() );
                            } else {
                                // alt is invalid, insert original instead
                                AtherysTowns.getInstance().getLogger().error( "[Wilderness] " + entry.getValue() + " is not a valid BlockType. Will save original instead.");
                                insertOne(transaction.getOriginal());
                            }
                            break;
                        }
                    }
                }
            });
        }
    }

    public void regenerate ( long timestamp ) {

    }

    private void insertOne ( BlockSnapshot snapshot ) {
        Document doc = new Document();

        // TODO: Serialize snapshot
        // ISSUE: When storing the snapshot, use the Location as a sort of primary key.
        // If 2 changes are made to the same location, only store the first change that was made ( ignore newer changes ).

        collection().insertOne( doc );
    }

    private static String snapshotToJson (BlockSnapshot snap) {
        Map<?, ?> serializedBlock = snap.toContainer().getMap(DataQuery.of()).get();
        return gson.toJson(serializedBlock);
    }

    private static BlockSnapshot jsonToSnapshot (String json)
    {
        Map<Object, Object> map = gson.fromJson(json, Map.class);
        DataContainer container = DataContainer.createNew();

        for (Map.Entry<Object, Object> entry : map.entrySet()) {
            container.set(DataQuery.of('.', entry.getKey().toString()), entry.getValue());
        }

        return BlockSnapshot.builder().build(container).get();
    }

    public static WildernessManager getInstance() {
        return instance;
    }

}
