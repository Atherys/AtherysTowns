package com.atherys.towns;

import com.atherys.core.utils.PluginConfig;
import com.atherys.towns.api.permission.nation.NationPermissions;
import com.atherys.towns.config.NationConfig;
import com.atherys.towns.config.NationRoleConfig;
import com.atherys.towns.config.TownConfig;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Singleton;
import ninja.leaping.configurate.objectmapping.Setting;
import org.spongepowered.api.service.economy.Currency;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
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

    @Setting("respawn-in-town")
    public boolean SPAWN_IN_TOWN = true;

    @Setting("currency")
    public Currency CURRENCY;

    @Setting("nation-leader-role")
    public String LEADER_ROLE = "leader";

    @Setting("nation-default-role")
    public String DEFAULT_ROLE = "citizen";

    @Setting("nation-roles")
    public Map<String, NationRoleConfig> NATION_ROLES = new HashMap<>();
    {
        NationRoleConfig nationLeader = new NationRoleConfig();
        nationLeader.setName("Leader");
        nationLeader.setTownPermissions(ImmutableSet.of(
                NationPermissions.INVITE_TOWN,
                NationPermissions.KICK_TOWN,
                NationPermissions.SET_PERMISSION,
                NationPermissions.SET_ROLE,
                NationPermissions.WITHDRAW_FROM_BANK,
                NationPermissions.DEPOSIT_INTO_BANK,
                NationPermissions.CHAT
        ));

        NationRoleConfig nationMember = new NationRoleConfig();
        nationMember.setName("Citizen");
        nationMember.setTownPermissions(ImmutableSet.of(
                NationPermissions.DEPOSIT_INTO_BANK,
                NationPermissions.CHAT
        ));

        NATION_ROLES.put("leader", nationLeader);
        NATION_ROLES.put("citizen", nationMember);
    }

    @Setting("town")
    public TownConfig TOWN = new TownConfig();

    protected TownsConfig() throws IOException {
        super("config/" + AtherysTowns.ID, "config.conf");
        init();
    }
}
