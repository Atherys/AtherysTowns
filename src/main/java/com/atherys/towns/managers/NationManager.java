package com.atherys.towns.managers;

import com.atherys.towns.db.TownsDatabase;
import com.atherys.towns.nation.Nation;
import com.atherys.towns.plot.Plot;
import com.atherys.towns.resident.Resident;
import com.atherys.towns.town.Town;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;

import java.util.Optional;
import java.util.UUID;

public final class NationManager extends AreaObjectManager<Nation> {

    private static NationManager instance = new NationManager();

    private NationManager() {
    }

    public Optional<Nation> getByPlot ( Plot plot ) {
        return getByTown(plot.getTown());
    }

    public Optional<Nation> getByResident ( Resident res ) {
        if ( res.getTown().isPresent() ) {
            return res.getTown().get().getParent();
        }
        return Optional.empty();
    }

    public Optional<Nation> getByTown ( Town town ) {
        return town.getParent();
    }

    @Override
    public MongoCollection<Document> collection() {
        return TownsDatabase.getInstance().getDatabase().getCollection("nations");
    }

    @Override
    public Document toDocument(Nation object) {
        Document doc = new Document("uuid", object.getUUID() );
        doc.append("name", object.getName());
        doc.append("tax", object.getTax());
        doc.append("leader_title", object.getLeaderTitle());
        doc.append("color", object.getColor().getId());
        doc.append("description", object.getDescription());

        return doc;
    }

    @Override
    public void saveAll() {
        super.saveAll(list);
    }

    @Override
    public boolean fromDocument(Document doc) {
        UUID uuid = doc.get("uuid", UUID.class);// UUID.fromString( doc.getString("uuid") );
        Nation.Builder builder = Nation.fromUUID(uuid);
        builder.name(doc.getString("name"));
        builder.tax(doc.getDouble("tax"));
        builder.leaderTitle(doc.getString("leader_title"));
        builder.color(Sponge.getGame().getRegistry().getType(TextColor.class, doc.getString("color")).orElse(TextColors.WHITE));
        builder.description(doc.getString("description"));

        builder.build();

        return true;
    }

    public static NationManager getInstance() {
        return instance;
    }
}