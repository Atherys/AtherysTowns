package com.atherys.towns.db;

import com.atherys.core.database.mongo.AbstractMongoDatabase;
import com.atherys.towns.AtherysTowns;

public class TownsDatabase extends AbstractMongoDatabase {

    private static TownsDatabase instance = new TownsDatabase();

    private TownsDatabase() {
        super( AtherysTowns.getConfig().DATABASE );
    }

    public static TownsDatabase getInstance() {
        return instance;
    }

}
