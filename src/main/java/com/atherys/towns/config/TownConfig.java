package com.atherys.towns.config;

import com.atherys.towns.api.permission.town.TownPermissions;
import com.atherys.towns.api.permission.world.WorldPermissions;
import com.google.common.collect.ImmutableSet;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.HashMap;
import java.util.Map;

@ConfigSerializable
public class TownConfig {

    @Setting("plot-max-area")
    public int MAX_PLOT_AREA = 1024;

    @Setting("plot-min-side")
    public int MIN_PLOT_SIDE = 16;

    @Setting("min-distance-to-town")
    public int MIN_DISTANCE_TO_TOWN = 100;

    @Setting("default-town-max-size")
    public int DEFAULT_TOWN_MAX_SIZE = 4096;

    @Setting("max-town-name-size")
    public int MAX_TOWN_NAME_LENGTH = 25;

    @Setting("town-spawn-cooldown-minutes")
    public int TOWN_COOLDOWN = 0;

    @Setting("town-spawn-warmup-seconds")
    public int TOWN_WARMUP = 0;

    @Setting("town-leader-role")
    public String TOWN_LEADER_ROLE = "mayor";

    @Setting("town-default-role")
    public String TOWN_DEFAULT_ROLE = "resident";

    @Setting("local-transactions")
    public boolean LOCAL_TRANSACTIONS = false;

    @Setting("roles")
    public Map<String, TownRoleConfig> ROLES = new HashMap<>();

    {
        TownRoleConfig townLeader = new TownRoleConfig();
        townLeader.setName("Mayor");
        townLeader.setTownPermissions(ImmutableSet.of(
                TownPermissions.RUIN_TOWN,
                TownPermissions.INVITE_RESIDENT,
                TownPermissions.KICK_RESIDENT,
                TownPermissions.CLAIM_PLOT,
                TownPermissions.UNCLAIM_PLOT,
                TownPermissions.GRANT_PLOT,
                TownPermissions.SET_PERMISSION,
                TownPermissions.WITHDRAW_FROM_BANK,
                TownPermissions.DEPOSIT_INTO_BANK,
                TownPermissions.JOIN_NATION,
                TownPermissions.SET_NAME,
                TownPermissions.SET_DESCRIPTION,
                TownPermissions.SET_MOTD,
                TownPermissions.SET_COLOR,
                TownPermissions.SET_FREELY_JOINABLE,
                TownPermissions.SET_SPAWN,
                TownPermissions.SET_PVP,
                TownPermissions.SET_ROLE,
                TownPermissions.TRANSFER_LEADERSHIP,
                TownPermissions.CHAT
        ));

        townLeader.setWorldPermissions(ImmutableSet.of(
                WorldPermissions.BUILD,
                WorldPermissions.DESTROY,
                WorldPermissions.DAMAGE_NONPLAYERS,
                WorldPermissions.DAMAGE_PLAYERS,
                WorldPermissions.INTERACT_TILE_ENTITIES,
                WorldPermissions.INTERACT_DOORS,
                WorldPermissions.INTERACT_REDSTONE,
                WorldPermissions.INTERACT_ENTITIES,
                WorldPermissions.SPAWN_ENTITIES
        ));

        TownRoleConfig townMember = new TownRoleConfig();
        townMember.setName("Resident");
        townMember.setTownPermissions(ImmutableSet.of(
                TownPermissions.DEPOSIT_INTO_BANK,
                TownPermissions.CHAT
        ));

        townMember.setWorldPermissions(ImmutableSet.of(
                WorldPermissions.BUILD,
                WorldPermissions.DESTROY,
                WorldPermissions.DAMAGE_NONPLAYERS,
                WorldPermissions.DAMAGE_PLAYERS,
                WorldPermissions.INTERACT_TILE_ENTITIES,
                WorldPermissions.INTERACT_DOORS,
                WorldPermissions.INTERACT_REDSTONE,
                WorldPermissions.INTERACT_ENTITIES,
                WorldPermissions.SPAWN_ENTITIES
        ));

        ROLES.put("mayor", townLeader);
        ROLES.put("resident", townMember);
    }
}
