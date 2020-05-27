package com.atherys.towns;

import com.atherys.core.utils.PluginConfig;
import com.atherys.towns.config.NationConfig;
import com.atherys.towns.config.TownConfig;
import com.google.inject.Singleton;
import ninja.leaping.configurate.objectmapping.Setting;
import org.spongepowered.api.service.economy.Currency;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@Singleton
public class TownsConfig extends PluginConfig {

    @Setting("max-residents-to-display")
    public int MAX_RESIDENTS_DISPLAY = 25;

    @Setting("economy-enabled")
    public boolean ECONOMY = true;

    @Setting("default-currency")
    public Currency DEFAULT_CURRENCY;

    @Setting("nations")
    public Set<NationConfig> NATIONS = new HashSet<>();
    {
        NATIONS.add(new NationConfig());
    }

    @Setting("town")
    public TownConfig TOWN = new TownConfig();

    protected TownsConfig() throws IOException {
        super("config/" + AtherysTowns.ID, "config.conf");
        init();
    }
}
