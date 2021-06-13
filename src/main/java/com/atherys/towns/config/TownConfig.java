package com.atherys.towns.config;

import com.atherys.towns.api.permission.TownsPermissionContexts;
import com.atherys.towns.api.permission.town.TownPermissions;
import com.atherys.towns.api.permission.world.WorldPermissions;
import com.google.common.collect.ImmutableSet;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@ConfigSerializable
public class TownConfig {

    @Setting("plot-max-area")
    public int MAX_PLOT_AREA = 1024;

    @Setting("plot-min-side")
    public int MIN_PLOT_SIDE = 16;

    @Setting("cuboid-plot-min-side")
    public int MIN_CUBOID_PLOT_SIDE = 7;

    @Setting("plot-selection-item")
    public ItemType PLOT_SELECTION_ITEM = ItemTypes.GOLDEN_AXE;

    @Setting("town-min-creation-distance")
    public int MIN_CREATION_DISTANCE = 100;

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

    @Setting("town-creation-cost")
    public double CREATION_COST = 0;

    @Setting("generate-roles")
    public boolean GENERATE_ROLES = true;

    @Setting("rent-config")
    public RentConfig RENT_CONFIG = new RentConfig();

    @Setting("default-plot-permissions")
    public Set<PlotPermissionConfig> DEFAULT_PLOT_PERMISSIONS = new HashSet<>();
    {
        DEFAULT_PLOT_PERMISSIONS.add(new PlotPermissionConfig(TownsPermissionContexts.TOWN, WorldPermissions.DESTROY));
        DEFAULT_PLOT_PERMISSIONS.add(new PlotPermissionConfig(TownsPermissionContexts.TOWN, WorldPermissions.BUILD));
        DEFAULT_PLOT_PERMISSIONS.add(new PlotPermissionConfig(TownsPermissionContexts.TOWN, WorldPermissions.DAMAGE_NONPLAYERS));
        DEFAULT_PLOT_PERMISSIONS.add(new PlotPermissionConfig(TownsPermissionContexts.TOWN, WorldPermissions.DAMAGE_PLAYERS));
        DEFAULT_PLOT_PERMISSIONS.add(new PlotPermissionConfig(TownsPermissionContexts.TOWN, WorldPermissions.INTERACT_ENTITIES));
        DEFAULT_PLOT_PERMISSIONS.add(new PlotPermissionConfig(TownsPermissionContexts.TOWN, WorldPermissions.INTERACT_TILE_ENTITIES));
        DEFAULT_PLOT_PERMISSIONS.add(new PlotPermissionConfig(TownsPermissionContexts.TOWN, WorldPermissions.INTERACT_DOORS));
        DEFAULT_PLOT_PERMISSIONS.add(new PlotPermissionConfig(TownsPermissionContexts.TOWN, WorldPermissions.INTERACT_REDSTONE));
        DEFAULT_PLOT_PERMISSIONS.add(new PlotPermissionConfig(TownsPermissionContexts.TOWN, WorldPermissions.SPAWN_ENTITIES));
    }

    @Setting("roles")
    public Map<String, TownRoleConfig> ROLES = new HashMap<>();

    {
        TownRoleConfig townLeader = new TownRoleConfig();
        townLeader.setName("Mayor");
        townLeader.setTownPermissions(ImmutableSet.of(
                TownPermissions.RUIN_TOWN,
                TownPermissions.START_RAID,
                TownPermissions.CANCEL_RAID,
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
