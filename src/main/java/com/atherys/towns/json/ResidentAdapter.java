package com.atherys.towns.json;

import com.atherys.core.database.mongo.MongoTypeAdapter;
import com.atherys.towns.model.Resident;
import org.bson.Document;

import java.util.Optional;

public class ResidentAdapter extends MongoTypeAdapter<Resident> {

    protected ResidentAdapter() {
        super(Resident.class);
    }

    @Override
    protected Optional<Document> toDocument(Resident resident) {
        return Optional.empty();
    }

    @Override
    protected Optional<Resident> fromDocument(Document document) {
        return Optional.empty();
    }
}
