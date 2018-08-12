package com.atherys.towns.json;

import com.atherys.core.database.mongo.MongoTypeAdapter;
import com.atherys.towns.model.Plot;
import org.bson.Document;

import java.util.Optional;

public class PlotAdapter extends MongoTypeAdapter<Plot> {

    protected PlotAdapter() {
        super(Plot.class);
    }

    @Override
    protected Optional<Document> toDocument(Plot o) {
        return Optional.empty();
    }

    @Override
    protected Optional fromDocument(Document document) {
        return Optional.empty();
    }
}
