package com.atherys.towns.utils;

import com.atherys.towns.nation.Nation;
import com.atherys.towns.plot.PlotDefinition;
import com.atherys.towns.plot.PlotFlags;
import com.flowpowered.math.vector.Vector3i;
import com.google.gson.*;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.List;
import java.util.Map;

public class Serialize {

    private static Gson gson = new Gson();

    public static String blockSnapshot (BlockSnapshot snap) {
        Map<?, ?> serializedBlock = snap.toContainer().getMap(DataQuery.of()).get();
        return gson.toJson(serializedBlock);
    }

    public static JsonObject vector3i ( Vector3i vector3i ) {
        JsonObject vectorObject = new JsonObject();

        vectorObject.addProperty("x", vector3i.getX() );
        vectorObject.addProperty("y", vector3i.getY() );
        vectorObject.addProperty("z", vector3i.getZ() );

        return vectorObject;
    }

    public static JsonObject location(Location loc) {
        JsonObject locObj = new JsonObject();
        locObj.addProperty("world", ( (World) loc.getExtent() ).getName() );
        locObj.addProperty("x", loc.getX() );
        locObj.addProperty("y", loc.getY() );
        locObj.addProperty("z", loc.getZ() );
        return locObj;
    }

    public static JsonObject plotFlags ( PlotFlags flags ) {
        JsonObject obj = new JsonObject();
        flags.getAll().forEach( (k, v) -> obj.addProperty(k.name(), v.name()) );
        return obj;
    }

    public static JsonObject definition(PlotDefinition definition) {
        JsonObject obj = new JsonObject();
        obj.addProperty("world", definition.getWorld().getUniqueId().toString() );
        obj.addProperty("x", definition.getX());
        obj.addProperty("y", definition.getY());
        obj.addProperty("w", definition.getWidth());
        obj.addProperty("h", definition.getHeight());
        return obj;
    }

    public static String color ( TextColor color ) {
        return color.getId().toLowerCase();
    }

    public static String nationList ( List<Nation> list ) {
        JsonArray arr = new JsonArray();
        for ( Nation n : list ) {
            arr.add(new JsonPrimitive(n.getUUID().toString()));
        }
        return arr.toString();
    }
}
