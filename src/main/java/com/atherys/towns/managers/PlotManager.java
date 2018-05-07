package com.atherys.towns.managers;

import com.atherys.towns.AtherysTowns;
import com.atherys.towns.plot.Plot;
import com.atherys.towns.plot.PlotBuilder;
import com.atherys.towns.plot.PlotDefinition;
import com.atherys.towns.plot.PlotFlags;
import com.atherys.towns.plot.flags.Extent;
import com.atherys.towns.plot.flags.ExtentRegistry;
import com.atherys.towns.plot.flags.Extents;
import com.atherys.towns.plot.flags.Flag;
import com.atherys.towns.plot.flags.FlagRegistry;
import com.atherys.towns.town.Town;
import com.atherys.towns.utils.DbUtils;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.bson.Document;

public final class PlotManager extends AreaObjectManager<Plot> {

    private static PlotManager instance = new PlotManager();

    private PlotManager() {
        super("plots");
    }

    public boolean checkIntersection(PlotDefinition definition) {
        for (Plot p : getAll()) {
            if (p.getDefinition().intersects(definition)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Optional<Document> toDocument(Plot object) {

        Document doc = new Document("uuid", object.getUUID());
        doc.append("town", object.getTown().getUUID());
        doc.append("name", object.getName());

        doc.append("definition", DbUtils.Serialize.definition(object.getDefinition()));

        Document flags = new Document();
        object.getFlags().getAll().forEach((k, v) -> flags.append(k.getId(), v.getId()));

        doc.append("flags", flags);

        return Optional.of(doc);
    }

    @Override
    public Optional<Plot> fromDocument(Document doc) {

        UUID uuid = doc.get("uuid", UUID.class); // UUID.fromString( doc.getString("uuid") );
        PlotBuilder builder = Plot.fromUUID(uuid);

        // load parent
        Optional<Town> parent = TownManager.getInstance().getByUUID(doc.get("town", UUID.class));
        if (parent.isPresent()) {
            builder.town(parent.get());
        } else {
            AtherysTowns.getInstance().getLogger().error(
                "[MongoDB] Plot load failure. Had invalid parent UUID, or parent had not been loaded yet.");
            return Optional.empty();
        }

        builder.name(doc.getString("name"));

        // load definition
        Optional<PlotDefinition> definition = DbUtils.Deserialize
            .definition(doc.get("definition", Document.class));
        if (!definition.isPresent()) {
            AtherysTowns.getInstance().getLogger()
                .error("[MongoDB] Plot load failure. Plot definition could not be deserialized.");
            return Optional.empty();
        }

        builder.definition(definition.get());

        // load flags
        Document flags = doc.get("flags", Document.class);
        PlotFlags plotFlags = PlotFlags.empty();
        for (Map.Entry<String, Object> flagData : flags.entrySet()) {
            Optional<Flag> flag = FlagRegistry.getInstance().getById(flagData.getKey());
            if (!flag.isPresent()) {
                continue;
            }
            Extent extent = ExtentRegistry.getInstance().getById((String) flagData.getValue())
                .orElse(Extents.NONE);
            plotFlags.set(flag.get(), extent);
        }
        builder.flags(plotFlags);

        return Optional.of(builder.build());
    }

    public static PlotManager getInstance() {
        return instance;
    }
}
