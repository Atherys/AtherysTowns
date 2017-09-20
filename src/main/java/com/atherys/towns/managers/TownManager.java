package com.atherys.towns.managers;

import com.atherys.towns.AtherysTowns;
import com.atherys.towns.db.TownsDatabase;
import com.atherys.towns.nation.Nation;
import com.atherys.towns.plot.PlotFlags;
import com.atherys.towns.town.Town;
import com.atherys.towns.town.TownStatus;
import com.flowpowered.math.vector.Vector3d;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;
import java.util.UUID;

public final class TownManager extends AreaObjectManager<Town> {

    private static TownManager instance = new TownManager();

    private TownManager() {
    }

    @Override
    public MongoCollection<Document> collection() {
        return TownsDatabase.getInstance().getDatabase().getCollection("towns");
    }

    @Override
    public Document toDocument(Town object) {
        Document doc = new Document("uuid", object.getUUID() );
        doc.append("nation", object.getParent().isPresent() ? object.getParent().get().getUUID() : null );
        doc.append("status", object.getStatus().id());
        doc.append("name", object.getName());

        Document flags = new Document();
        object.getTownFlags().getAll().forEach( (k,v) -> flags.append(k.name(), v.name()));
        doc.append("flags", flags);

        doc.append("max_size", object.getMaxSize());

        Vector3d spawnPosition = object.getSpawn().getPosition();
        doc.append("spawn", new Document()
            .append("world", object.getSpawn().getExtent().getUniqueId().toString() )
            .append("x", spawnPosition.getX())
            .append("y", spawnPosition.getY())
            .append("z", spawnPosition.getZ())
        );

        doc.append("color", object.getColor().getId());
        doc.append("motd", object.getMOTD());
        doc.append("description", object.getDescription());

        return doc;
    }

    @Override
    public void saveAll() {
        super.saveAll(list);
    }

    @Override
    public boolean fromDocument(Document doc) {
        Town.Builder builder = Town.fromUUID( doc.get("uuid", UUID.class) ); // UUID.fromString(doc.getString("uuid")));

        UUID nation_uuid = doc.get("nation", UUID.class);
        if ( nation_uuid != null ) {
            Optional<Nation> nation = NationManager.getInstance().getByUUID( nation_uuid );
            nation.ifPresent( n -> {
                builder.nation( n );
                builder.status(TownStatus.fromId(doc.getInteger("status")));
            });
        }

        builder.name(doc.getString("name"));

        // load flags
        Document flags = doc.get("flags", Document.class);
        PlotFlags plotFlags = PlotFlags.regular();
        flags.forEach( (k,v) -> plotFlags.set(PlotFlags.Flag.valueOf(k), PlotFlags.Extent.valueOf((String) v)));
        builder.flags(plotFlags);

        builder.maxSize(doc.getInteger("max_size"));

        // load spawn
        Document spawnDoc = doc.get("spawn", Document.class);
        Vector3d spawnPos = new Vector3d(
                spawnDoc.getDouble("x"),
                spawnDoc.getDouble("y"),
                spawnDoc.getDouble("z")
        );
        Optional<World> spawnWorld = Sponge.getServer().getWorld( UUID.fromString( spawnDoc.getString("world") ) );
        if ( spawnWorld.isPresent() ) {
            Location<World> spawn = new Location<>( spawnWorld.get(), spawnPos );
            builder.spawn(spawn);
        } else {
            AtherysTowns.getInstance().getLogger().error("[MongoDB] Town load failure. Had improper spawn.");
            return false;
        }

        builder.color( Sponge.getGame().getRegistry().getType(TextColor.class, doc.getString("color")).orElse(TextColors.WHITE)  );
        builder.motd(doc.getString("motd"));
        builder.description(doc.getString("description"));

        builder.build();

        return true;
    }

    public static TownManager getInstance() {
        return instance;
    }
}
