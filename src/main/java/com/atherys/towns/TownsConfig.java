package com.atherys.towns;

import com.atherys.core.database.mongo.MongoDatabaseConfig;
import com.atherys.core.utils.PluginConfig;
import ninja.leaping.configurate.objectmapping.Setting;

import java.io.IOException;

public class TownsConfig extends PluginConfig {

    @Setting("database")
    public MongoDatabaseConfig DATABASE;

    protected TownsConfig(String directory, String filename) throws IOException {
        super(directory, filename);
    }
}
