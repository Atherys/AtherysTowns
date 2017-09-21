package com.atherys.towns.managers;

import com.atherys.towns.AtherysTowns;
import com.atherys.towns.db.TownsDatabase;
import com.atherys.towns.plot.Plot;
import com.atherys.towns.plot.PlotDefinition;
import com.atherys.towns.plot.PlotFlags;
import com.atherys.towns.town.Town;
import com.atherys.towns.utils.DbUtils;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

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

        doc.append("definition", DbUtils.Serialize.definition(object.getDefinition()) );

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
        Optional<PlotDefinition> definition = DbUtils.Deserialize.definition(doc.get("definition", Document.class));
        if ( !definition.isPresent() ) {
            AtherysTowns.getInstance().getLogger().error("[MongoDB] Plot load failure.");
            return false;
        }

        builder.definition(definition.get());

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
