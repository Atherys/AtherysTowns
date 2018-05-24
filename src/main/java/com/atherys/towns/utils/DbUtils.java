package com.atherys.towns.utils;

import com.atherys.towns.AtherysTowns;
import com.atherys.towns.api.plot.PlotDefinition;
import com.flowpowered.math.vector.Vector3d;
import com.google.gson.Gson;
import org.bson.Document;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class DbUtils {

    private static Gson gson = new Gson();

    public static class Serialize {

        public static Document location(Location<World> location) {
            Vector3d position = location.getPosition();
            return new Document()
                    .append("world", location.getExtent().getUniqueId().toString())
                    .append("x", position.getX())
                    .append("y", position.getY())
                    .append("z", position.getZ());
        }

        public static Document definition(PlotDefinition definition) {
            return new Document("world", definition.getWorld().getUniqueId().toString())
                    .append("x", definition.getX())
                    .append("y", definition.getY())
                    .append("w", definition.getWidth())
                    .append("h", definition.getHeight());
        }

        public static String snapshot(BlockSnapshot snap) {
            Map<?, ?> serializedBlock = snap.toContainer().getMap(DataQuery.of()).get();
            return gson.toJson(serializedBlock);
        }

    }

    public static class Deserialize {

        public static Optional<Location<World>> location(Document doc) {
            Vector3d position = new Vector3d(
                    doc.getDouble("x"),
                    doc.getDouble("y"),
                    doc.getDouble("z")
            );
            Optional<World> worldOpt = Sponge.getServer()
                    .getWorld(UUID.fromString(doc.getString("world")));
            return worldOpt.map(world1 -> new Location<>(world1, position));
        }

        public static Optional<PlotDefinition> definition(Document doc) {
            Optional<World> world = Sponge.getServer()
                    .getWorld(UUID.fromString(doc.getString("world")));
            if (world.isPresent()) {
                PlotDefinition define = new PlotDefinition(
                        world.get(),
                        doc.getDouble("x"),
                        doc.getDouble("y"),
                        doc.getDouble("w"),
                        doc.getDouble("h")
                );
                return Optional.of(define);
            } else {
                AtherysTowns.getInstance().getLogger()
                        .error("[MongoDB] Failed to deserialize plot definition.");
                return Optional.empty();
            }
        }

        public static BlockSnapshot snapshot(String json) {
            Map<Object, Object> map = gson.fromJson(json, Map.class);
            DataContainer container = DataContainer.createNew();

            for (Map.Entry<Object, Object> entry : map.entrySet()) {
                container.set(DataQuery.of('.', entry.getKey().toString()), entry.getValue());
            }

            return BlockSnapshot.builder().build(container).get();
        }

    }

}
