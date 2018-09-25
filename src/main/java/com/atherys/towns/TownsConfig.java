package com.atherys.towns;

import com.atherys.core.database.mongo.MongoDatabaseConfig;
import com.atherys.core.utils.PluginConfig;
import com.atherys.towns.config.RankConfig;
import ninja.leaping.configurate.objectmapping.Setting;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;

import javax.print.attribute.standard.MediaSize;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

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

    @Setting("nation_ranks")
    public Set<RankConfig> NATION_RANKS = new HashSet<>();

    {
        NATION_RANKS.add(RankConfig.builder()
                .id("nation_leader")
                .name("Nation Leader")
                .permission("atherystowns.nation.bank.deposit")
                .permission("atherystowns.nation.bank.withdraw")
                .permission("atherystowns.nation.set.nation_tax")
                .permission("atherystowns.nation.set.town_tax")
                .permission("atherystowns.nation.set.name")
                .permission("atherystowns.nation.set.description")
                .permission("atherystowns.nation.set.color")
                .permission("atherystowns.nation.set.capital")
                .build()
        );
    }

    @Setting("town_ranks")
    public Set<RankConfig> TOWN_RANKS = new HashSet<>();

    {
        TOWN_RANKS.add(RankConfig.builder()
                .id("mayor")
                .name("Mayor")
                .permission("atherystowns.town.bank.deposit")
                .permission("atherystowns.town.bank.withdraw")
                .build()
        );
    }

    protected TownsConfig() throws IOException {
        super("config/" + AtherysTowns.ID, "config.conf");
    }
}
