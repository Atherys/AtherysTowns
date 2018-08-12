package com.atherys.towns.db;

import com.atherys.core.database.mongo.AbstractMongoDatabaseManager;
import com.atherys.towns.model.Town;
import com.google.inject.Inject;
import org.bson.Document;
import org.slf4j.Logger;

import java.util.Optional;

public class TownManager extends AbstractMongoDatabaseManager<Town> {

    @Inject
    protected TownManager(Logger logger, TownsDatabase mongoDatabase) {
        super(logger, mongoDatabase, Town.class);
        this.loadAll();
    }

    @Override
    protected Optional<Document> toDocument(Town object) {
        return super.toDocument(object);
    }

    @Override
    protected Optional<Town> fromDocument(Document doc) {
        return super.fromDocument(doc);
    }
}
