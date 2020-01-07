package com.atherys.towns;

import com.atherys.core.utils.PluginConfig;
import com.atherys.towns.api.permission.Permission;
import com.atherys.towns.api.permission.nation.NationPermissions;
import com.atherys.towns.api.permission.town.TownPermissions;
import com.atherys.towns.api.permission.world.WorldPermissions;
import com.atherys.towns.config.RolesConfig;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Singleton;
import ninja.leaping.configurate.objectmapping.Setting;
import org.spongepowered.api.service.economy.Currency;

import java.io.IOException;
import java.util.Set;

@Singleton
public class TownsConfig extends PluginConfig {

    @Setting("plot-max-area")
    public int MAX_PLOT_AREA = 1024;

    @Setting("plot-min-side")
    public int MIN_PLOT_SIDE = 16;

    @Setting("default-town-max-size")
    public int DEFAULT_TOWN_MAX_SIZE = 4096;

    @Setting("max-town-name-size")
    public int MAX_TOWN_NAME_LENGTH = 25;

    @Setting("max-nation-name-size")
    public int MAX_NATION_NAME_LENGTH = 25;

    @Setting("max-residents-to-display")
    public int MAX_RESIDENTS_DISPLAY = 25;

    @Setting("economy-enabled")
    public boolean ECONOMY = true;

    @Setting("require-being-in-town-for-transactions")
    public boolean LOCAL_TRANSACTIONS = false;

    @Setting("currency")
    public Currency CURRENCY;

    @Setting("town-spawn-cooldown-minutes")
    public int TOWN_COOLDOWN = 0;

    @Setting("town-spawn-warmup-seconds")
    public int TOWN_WARMUP = 0;

    @Setting("roles")
    public RolesConfig ROLES = new RolesConfig();

    protected TownsConfig() throws IOException {
        super("config/" + AtherysTowns.ID, "config.conf");
        init();
    }
}
