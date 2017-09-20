package com.atherys.towns.managers;

import com.atherys.towns.AtherysTowns;
import com.atherys.towns.db.TownsDatabase;
import com.atherys.towns.plot.Plot;
import com.atherys.towns.plot.PlotDefinition;
import com.atherys.towns.plot.PlotFlags;
import com.atherys.towns.town.Town;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.world.World;

import java.util.Optional;
import java.util.UUID;

public final class PlotManager extends AreaObjectManager<Plot> {

    private static PlotManager instance = new PlotManager();

    private PlotManager() {
    }

    public boolean checkIntersection (PlotDefinition definition ) {
        for ( Plot p : list ) {
            if ( p.getDefinition().intersects(definition) ) return true;
        }
        return false;
    }

    @Override
    public MongoCollection<Document> collection() {
        return TownsDatabase.getInstance().getDatabase().getCollection("plots");
    }

    @Override
    public Document toDocument(Plot object) {

        Document doc = new Document("uuid", object.getUUID() );
        doc.append("town", object.getTown().getUUID() );
        doc.append("name", object.getName());

        Document definition = new Document()
                .append("world", object.getDefinition().getWorld().getUniqueId().toString())
                .append("x", object.getDefinition().getX())
                .append("y", object.getDefinition().getY())
                .append("w", object.getDefinition().getWidth())
                .append("h", object.getDefinition().getHeight());

        doc.append("definition", definition);

        Document flags = new Document();
        object.getFlags().getAll().forEach( (k,v) -> flags.append(k.name(), v.name()));

        doc.append("flags", flags);

        return doc;
    }

    @Override
    public void saveAll() {
        super.saveAll(list);
    }

    @Override
    public boolean fromDocument(Document doc) {

        UUID uuid = doc.get("uuid", UUID.class); // UUID.fromString( doc.getString("uuid") );
        Plot.Builder builder = Plot.fromUUID(uuid);

        // load parent
        Optional<Town> parent = TownManager.getInstance().getByUUID( doc.get("town", UUID.class) );
        if ( parent.isPresent() ) {
            builder.town(parent.get());
        } else {
            AtherysTowns.getInstance().getLogger().error("[MongoDB] Plot load failure. Had invalid parent UUID, or parent had not been loaded yet.");
            return false;
        }

        builder.name(doc.getString("name"));

        // load definition
        Document definition = doc.get("definition", Document.class);
        Optional<World> world = Sponge.getServer().getWorld( UUID.fromString( definition.getString("world") ) );
        if ( world.isPresent() ) {
            PlotDefinition define = new PlotDefinition(
                    world.get(),
                    definition.getDouble("x"),
                    definition.getDouble("y"),
                    definition.getDouble("w"),
                    definition.getDouble("h")
            );
            builder.definition(define);
        } else {
            AtherysTowns.getInstance().getLogger().error("[MongoDB] Plot load failure. Had improper definition.");
            return false;
        }

        // load flags
        Document flags = doc.get("flags", Document.class);
        PlotFlags plotFlags = PlotFlags.empty();
        flags.forEach( (k,v) -> plotFlags.set(PlotFlags.Flag.valueOf(k), PlotFlags.Extent.valueOf((String) v)));
        builder.flags(plotFlags);

        builder.build();

        // TODO: Convert BSON to Plot
        return true;
    }

    public static PlotManager getInstance() {
        return instance;
    }
}
