package com.atherys.towns.db;

import com.atherys.core.database.mongo.AbstractMongoDatabaseManager;
import com.atherys.towns.model.Resident;
import org.bson.Document;
import org.slf4j.Logger;
import org.spongepowered.api.entity.living.player.User;

import java.util.Optional;
import java.util.UUID;

public class ResidentManager extends AbstractMongoDatabaseManager<Resident> {

    protected ResidentManager(Logger logger, TownsDatabase mongoDatabase) {
        super(logger, mongoDatabase, Resident.class);
        this.loadAll();
    }

    public <T extends User> Resident getOrCreate(T player) {
        return getOrCreate(player.getUniqueId());
    }

    public Resident getOrCreate(UUID playerUUID) {
        return get(playerUUID).orElseGet(() -> {
            Resident resident = new Resident(playerUUID);
            save(resident);
            return resident;
        });
    }

    @Override
    protected Optional<Document> toDocument(Resident object) {
        return super.toDocument(object);
    }

    @Override
    protected Optional<Resident> fromDocument(Document doc) {
        return super.fromDocument(doc);
    }
}
