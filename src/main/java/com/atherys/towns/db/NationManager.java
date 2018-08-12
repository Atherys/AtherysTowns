package com.atherys.towns.db;

import com.atherys.core.database.mongo.AbstractMongoDatabaseManager;
import com.atherys.towns.model.Nation;
import org.bson.Document;
import org.slf4j.Logger;

import java.util.Optional;

public class NationManager extends AbstractMongoDatabaseManager<Nation> {

    protected NationManager(Logger logger, TownsDatabase mongoDatabase) {
        super(logger, mongoDatabase, Nation.class);
        this.loadAll();
    }

    @Override
    protected Optional<Document> toDocument(Nation object) {
        return super.toDocument(object);
    }

    @Override
    protected Optional<Nation> fromDocument(Document doc) {
        return super.fromDocument(doc);
    }
}
