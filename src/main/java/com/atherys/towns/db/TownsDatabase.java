package com.atherys.towns.db;

import com.atherys.core.database.mongo.AbstractMongoDatabase;
import com.atherys.towns.TownsConfig;
import com.google.inject.Inject;

public class TownsDatabase extends AbstractMongoDatabase {

    @Inject
    private TownsDatabase(TownsConfig config) {
        super(config.DATABASE);
    }

}
