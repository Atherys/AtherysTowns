package com.atherys.towns.utils;

import com.atherys.towns.AtherysTowns;
import com.atherys.towns.nation.Nation;
import com.atherys.towns.plot.PlotDefinition;
import com.atherys.towns.plot.PlotFlags;
import com.flowpowered.math.vector.Vector3i;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.MemoryDataContainer;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.*;

public class Deserialize {

    private static final Gson gson = new Gson();
    private static final JsonParser parser = new JsonParser();

    public static BlockSnapshot blockSnapshot(String json)
    {
        Map<Object, Object> map = gson.fromJson(json, Map.class);
        DataContainer container = new MemoryDataContainer();

        for (Map.Entry<Object, Object> entry : map.entrySet())
        {
            container.set(DataQuery.of('.', entry.getKey().toString()), entry.getValue());
        }

        return BlockSnapshot.builder().build(container).get();
    }

    public static Optional<Location> location ( String jsonString ) {
        return location(parser.parse(jsonString).getAsJsonObject());
    }

    public static Optional<Location> location (JsonObject locJson) {
        if ( locJson.isJsonNull() ) return Optional.empty();
        if ( !locJson.has("world") || !locJson.has("x") || !locJson.has("y") || !locJson.has("z") ) return Optional.empty();
        Optional<World> world = AtherysTowns.getInstance().getGame().getServer().getWorld(locJson.get("world").getAsString());
        if ( !world.isPresent() ) return Optional.empty();

        double x = locJson.get("x").getAsDouble();
        double y = locJson.get("y").getAsDouble();
        double z = locJson.get("z").getAsDouble();

        return Optional.of(new Location<>(world.get(), x, y, z) );
    }

    public static Optional<Vector3i> vector3i ( String jsonString ) {
        return vector3i(parser.parse(jsonString).getAsJsonObject());
    }

    public static Optional<Vector3i> vector3i ( JsonObject vectorJson ) {
        if ( vectorJson.isJsonNull() ) return Optional.empty();
        if ( !vectorJson.has("x") || !vectorJson.has("y") || !vectorJson.has("z") ) return Optional.empty();
        int x = vectorJson.get("x").getAsInt();
        int y = vectorJson.get("y").getAsInt();
        int z = vectorJson.get("z").getAsInt();
        return Optional.of( Vector3i.from(x, y, z) );
    }

    public static PlotFlags plotFlags ( String flags ) {
        return plotFlags(parser.parse(flags).getAsJsonObject());
    }

    public static PlotFlags plotFlags ( JsonObject flags ) {
        PlotFlags plotFlags = PlotFlags.regular();
        if ( flags.isJsonNull() ) return plotFlags;
        for ( PlotFlags.Flag flag : PlotFlags.Flag.values() ) {
            plotFlags.set(flag, PlotFlags.Extent.valueOf( flags.get( flag.name() ).getAsString( ) ) );
        }

        return plotFlags;
    }

    public static Optional<PlotDefinition> definition ( String defineString ) {
        return definition(parser.parse(defineString).getAsJsonObject());
    }

    private static Optional<PlotDefinition> definition ( JsonObject define ) {
        if ( define.isJsonNull() ) return Optional.empty();
        if ( !define.has("x") || !define.has("y") || !define.has("w") || !define.has("h") ) return Optional.empty();
        Optional<World> world = Sponge.getServer().getWorld(UUID.fromString(define.get("world").getAsString()));
        return world.map(world1 -> new PlotDefinition(world1, define.get("x").getAsDouble(), define.get("y").getAsDouble(), define.get("w").getAsDouble(), define.get("h").getAsDouble()));
    }

    public static TextColor color (String color) {
        return AtherysTowns.getInstance().getGame().getRegistry().getType(TextColor.class, color).orElse(TextColors.WHITE);
    }

    public static List<Nation> nationList (String natList ) {
        List<Nation> list = new LinkedList<>();
        JsonElement el = parser.parse(natList);
        if ( el.isJsonArray() ) {
            for ( JsonElement e : el.getAsJsonArray() ) {
                Optional<Nation> nOpt = AtherysTowns.getInstance().getNationManager().getByUUID(UUID.fromString(e.getAsString()));
                nOpt.ifPresent(list::add);
            }
        }
        return list;
    }

}
