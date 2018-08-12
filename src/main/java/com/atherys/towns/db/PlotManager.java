package com.atherys.towns.db;

import com.atherys.core.database.mongo.AbstractMongoDatabaseManager;
import com.atherys.towns.model.Plot;
import com.google.inject.Inject;
import org.bson.Document;
import org.slf4j.Logger;

import java.util.Optional;

public class PlotManager extends AbstractMongoDatabaseManager<Plot> {

    @Inject
    protected PlotManager(Logger logger, TownsDatabase mongoDatabase) {
        super(logger, mongoDatabase, Plot.class);
        this.loadAll();
    }

    @Override
    protected Optional<Document> toDocument(Plot object) {
        return Optional.of(new PlotDocument(object));
    }

    @Override
    protected Optional<Plot> fromDocument(Document doc) {
        return super.fromDocument(doc);
    }
}
