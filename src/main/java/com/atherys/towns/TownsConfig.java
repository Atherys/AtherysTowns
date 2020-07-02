package com.atherys.towns;

import com.atherys.core.utils.PluginConfig;
import com.atherys.towns.config.NationConfig;
import com.atherys.towns.config.RaidConfig;
import com.atherys.towns.config.TownConfig;
import com.google.inject.Singleton;
import ninja.leaping.configurate.objectmapping.Setting;
import org.spongepowered.api.service.economy.Currency;

import java.io.IOException;


@Singleton
public class TownsConfig extends PluginConfig {

    @Setting("max-residents-to-display")
    public int MAX_RESIDENTS_DISPLAY = 25;

    @Setting("economy-enabled")
    public boolean ECONOMY = true;

    @Setting("default-currency")
    public Currency DEFAULT_CURRENCY;

    @Setting("min-residents-to-create-town")
    public int MIN_RESIDENTS_TOWN_CREATE = 3;

    @Setting("tax-collection-interval-minutes")
    public int TAX_COLLECTION_INTERVAL = 60;

    @Setting("nation-chat-prefix")
    public String NATION_CHAT_PREFIX = "&f[&eN&f]&r";

    @Setting("town-chat-prefix")
    public String TOWN_CHAT_PREFIX = "&f[&bT&f]&r";

    @Setting("respawn-in-town")
    public boolean SPAWN_IN_TOWN = true;

    @Setting("raid")
    public RaidConfig RAID = new RaidConfig();

    @Setting("nation")
    public NationConfig NATION = new NationConfig();

    @Setting("town")
    public TownConfig TOWN = new TownConfig();

    protected TownsConfig() throws IOException {
        super("config/" + AtherysTowns.ID, "config.conf");
        init();
    }
}
