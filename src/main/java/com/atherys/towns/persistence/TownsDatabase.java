package com.atherys.towns.persistence;

import com.atherys.core.database.mongo.MorphiaDatabase;
import com.atherys.towns.AtherysTowns;
import com.atherys.towns.persistence.converter.TextColorConverter;
import com.atherys.towns.persistence.converter.WorldTypeConverter;

public class TownsDatabase extends MorphiaDatabase {

    private static TownsDatabase instance;

    protected TownsDatabase() {
        super(AtherysTowns.getConfig().DATABASE, "com.atherys.towns.model");
        getMorphia().getMapper().getConverters().addConverter(new WorldTypeConverter());
        getMorphia().getMapper().getConverters().addConverter(new TextColorConverter());
    }

    public static TownsDatabase getInstance() {
        return instance;
    }
}
