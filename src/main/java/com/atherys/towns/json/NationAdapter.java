package com.atherys.towns.json;

import com.atherys.core.database.mongo.MongoTypeAdapter;
import com.atherys.towns.model.Nation;
import org.bson.Document;

import java.util.Optional;

public class NationAdapter extends MongoTypeAdapter<Nation> {

    protected NationAdapter() {
        super(Nation.class);
    }

    @Override
    protected Optional<Document> toDocument(Nation nation) {
        return Optional.empty();
    }

    @Override
    protected Optional<Nation> fromDocument(Document document) {
        return Optional.empty();
    }
}
