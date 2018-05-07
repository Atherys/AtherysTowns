package com.atherys.towns.managers;

import com.atherys.towns.nation.Nation;
import com.atherys.towns.nation.NationBuilder;
import com.atherys.towns.plot.Plot;
import com.atherys.towns.resident.Resident;
import com.atherys.towns.town.Town;
import org.bson.Document;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;

import java.util.Optional;
import java.util.UUID;

/**
 * An implementation of {@link AreaObjectManager} for {@link Nation}s
 */
public final class NationManager extends AreaObjectManager<Nation> {

    private static NationManager instance = new NationManager();

    private NationManager() {
        super("nations");
    }

    public static NationManager getInstance() {
        return instance;
    }

    /**
     * Retrieves a {@link Nation} based on the plot
     *
     * @param plot The plot whose nation is to be retrieved
     * @return An optional containing the nation.
     */
    public Optional<Nation> getByPlot(Plot plot) {
        return getByTown(plot.getTown());
    }

    /**
     * Retrieves a {@link Nation} based on the resident
     *
     * @param res The resident whose nation is to be retrieved
     * @return An optional containing the nation.
     */
    public Optional<Nation> getByResident(Resident res) {
        if (res.getTown().isPresent()) {
            return res.getTown().get().getParent();
        }
        return Optional.empty();
    }

    /**
     * Retrieves a {@link Nation} based on the town
     *
     * @param town The town whose nation is to be retrieved
     * @return An optional containing the nation.
     */
    public Optional<Nation> getByTown(Town town) {
        return town.getParent();
    }

    /**
     * Serialize the provided {@link Nation} into a {@link Document} for the purposes of storing it to
     * the database.
     *
     * @param object The Nation to be serialized
     * @return The serialized nation
     */
    @Override
    public Optional<Document> toDocument(Nation object) {
        Document doc = new Document("uuid", object.getUUID());
        doc.append("name", object.getName());
        doc.append("tax", object.getTax());
        doc.append("leader_title", object.getLeaderTitle());
        doc.append("color", object.getColor().getId());
        doc.append("description", object.getDescription());

        return Optional.of(doc);
    }

    /**
     * Deserialize the provided {@link Document} into a {@link Nation}.
     *
     * @param doc The Document to be deserialized
     * @return The deserialized nation
     */
    @Override
    public Optional<Nation> fromDocument(Document doc) {
        UUID uuid = doc.get("uuid", UUID.class);// UUID.fromString( doc.getString("uuid") );
        NationBuilder builder = Nation.fromUUID(uuid);
        builder.name(doc.getString("name"));
        builder.tax(doc.getDouble("tax"));
        builder.leaderTitle(doc.getString("leader_title"));
        builder.color(
                Sponge.getGame().getRegistry().getType(TextColor.class, doc.getString("color"))
                        .orElse(TextColors.WHITE));
        builder.description(doc.getString("description"));

        return Optional.of(builder.build());
    }
}