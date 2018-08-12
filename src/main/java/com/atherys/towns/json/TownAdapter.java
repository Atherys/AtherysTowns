package com.atherys.towns.json;

import com.atherys.core.database.mongo.MongoTypeAdapter;
import com.atherys.towns.model.Town;
import org.bson.Document;

import java.util.Optional;

public class TownAdapter extends MongoTypeAdapter<Town> {

    protected TownAdapter() {
        super(Town.class);
    }

    @Override
    protected Optional<Document> toDocument(Town town) {
        return Optional.empty();
    }

    @Override
    protected Optional<Town> fromDocument(Document document) {
        return Optional.empty();
    }
}
