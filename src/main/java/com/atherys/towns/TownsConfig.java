package com.atherys.towns;

import com.atherys.core.database.mongo.MongoDatabaseConfig;
import com.atherys.core.utils.PluginConfig;
import ninja.leaping.configurate.objectmapping.Setting;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;

import java.io.IOException;

public class TownsConfig extends PluginConfig {

    @Setting("database")
    public MongoDatabaseConfig DATABASE = new MongoDatabaseConfig();

    @Setting("default_town_name")
    public String DEFAULT_TOWN_NAME = "Town Name";

    @Setting("default_town_size")
    public int DEFAULT_TOWN_SIZE = 2048;

    @Setting("default_town_description")
    public String DEFAULT_TOWN_DESCRIPTION = "Default town description";

    @Setting("default_town_motd")
    public String DEFAULT_TOWN_MOTD = "Default town motd";

    @Setting("default_town_color")
    public TextColor DEFAULT_TOWN_COLOR = TextColors.WHITE;

    protected TownsConfig() throws IOException {
        super("config/" + AtherysTowns.ID, "config.conf");
    }
}
