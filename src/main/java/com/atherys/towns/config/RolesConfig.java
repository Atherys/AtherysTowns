package com.atherys.towns.config;

import com.atherys.towns.api.permission.nation.NationPermissions;
import com.atherys.towns.api.permission.town.TownPermissions;
import com.atherys.towns.api.permission.world.WorldPermissions;
import com.google.common.collect.ImmutableSet;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.HashSet;
import java.util.Set;

@ConfigSerializable
public class RolesConfig {

    @Setting("town-leader-role")
    private String townLeaderRoleShortName = "mayor";

    @Setting("nation-default-role")
    private String townDefaultRoleShortName = "member";

    @Setting("nation-leader-role")
    private String nationLeaderRoleShortName = "leader";

    @Setting("nation-default-role")
    private String nationDefaultRoleShortName = "citizen";

    @Setting("town-roles")
    private Set<TownRoleConfig> townRoleConfigs = new HashSet<>();

    @Setting("nation-roles")
    private Set<NationRoleConfig> nationRoleConfigs = new HashSet<>();

    {
        TownRoleConfig townLeader = new TownRoleConfig();
        townLeader.setShortName("mayor");
        townLeader.setName("Mayor");
        townLeader.setTownPermissions(ImmutableSet.of(
                TownPermissions.INVITE_RESIDENT,
                TownPermissions.KICK_RESIDENT,
                TownPermissions.CLAIM_PLOT,
                TownPermissions.UNCLAIM_PLOT,
                TownPermissions.GRANT_PLOT,
                TownPermissions.ADD_PERMISSION,
                TownPermissions.REVOKE_PERMISSION,
                TownPermissions.WITHDRAW_FROM_BANK,
                TownPermissions.DEPOSIT_INTO_BANK,
                TownPermissions.LEAVE_NATION,
                TownPermissions.JOIN_NATION,
                TownPermissions.SET_NAME,
                TownPermissions.SET_DESCRIPTION,
                TownPermissions.SET_MOTD,
                TownPermissions.SET_COLOR,
                TownPermissions.SET_FREELY_JOINABLE,
                TownPermissions.SET_SPAWN,
                TownPermissions.SET_PVP,
                TownPermissions.TRANSFER_LEADERSHIP,
                TownPermissions.CHAT
        ));

        townLeader.setWorldPermissions(ImmutableSet.of(
                WorldPermissions.BUILD,
                WorldPermissions.DESTROY,
                WorldPermissions.DAMAGE_NONPLAYERS,
                WorldPermissions.DAMAGE_PLAYERS,
                WorldPermissions.INTERACT_CHESTS,
                WorldPermissions.INTERACT_DOORS,
                WorldPermissions.INTERACT_REDSTONE,
                WorldPermissions.INTERACT_ENTITIES
        ));

        TownRoleConfig townMember = new TownRoleConfig();
        townMember.setShortName("resident");
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
                WorldPermissions.INTERACT_CHESTS,
                WorldPermissions.INTERACT_DOORS,
                WorldPermissions.INTERACT_REDSTONE,
                WorldPermissions.INTERACT_ENTITIES
        ));

        NationRoleConfig nationLeader = new NationRoleConfig();
        nationLeader.setShortName("leader");
        nationLeader.setName("Leader");
        nationLeader.setTownPermissions(ImmutableSet.of(
                NationPermissions.INVITE_TOWN,
                NationPermissions.KICK_TOWN,
                NationPermissions.ADD_PERMISSION,
                NationPermissions.REVOKE_PERMISSION,
                NationPermissions.WITHDRAW_FROM_BANK,
                NationPermissions.DEPOSIT_INTO_BANK,
                NationPermissions.SET_NAME,
                NationPermissions.SET_DESCRIPTION,
                NationPermissions.SET_FREELY_JOINABLE,
                NationPermissions.ADD_ALLY,
                NationPermissions.ADD_ENEMY,
                NationPermissions.ADD_NEUTRAL,
                NationPermissions.DECLARE_WAR,
                NationPermissions.DECLARE_PEACE,
                NationPermissions.TRANSFER_LEADERSHIP,
                NationPermissions.CHAT
        ));

        NationRoleConfig nationMember = new NationRoleConfig();
        nationLeader.setShortName("citizen");
        nationLeader.setName("Citizen");
        nationLeader.setTownPermissions(ImmutableSet.of(
                NationPermissions.DEPOSIT_INTO_BANK,
                NationPermissions.CHAT
        ));

        townRoleConfigs.add(townLeader);
        townRoleConfigs.add(townMember);

        nationRoleConfigs.add(nationLeader);
        nationRoleConfigs.add(nationMember);
    }

    public RolesConfig() {
    }

    public String getTownDefaultRoleShortName() {
        return townDefaultRoleShortName;
    }

    public void setTownDefaultRoleShortName(String townDefaultRoleShortName) {
        this.townDefaultRoleShortName = townDefaultRoleShortName;
    }

    public String getNationDefaultRoleShortName() {
        return nationDefaultRoleShortName;
    }

    public void setNationDefaultRoleShortName(String nationDefaultRoleShortName) {
        this.nationDefaultRoleShortName = nationDefaultRoleShortName;
    }

    public String getTownLeaderRoleShortName() {
        return townLeaderRoleShortName;
    }

    public void setTownLeaderRoleShortName(String townLeaderRoleShortName) {
        this.townLeaderRoleShortName = townLeaderRoleShortName;
    }

    public String getNationLeaderRoleShortName() {
        return nationLeaderRoleShortName;
    }

    public void setNationLeaderRoleShortName(String nationLeaderRoleShortName) {
        this.nationLeaderRoleShortName = nationLeaderRoleShortName;
    }

    public Set<TownRoleConfig> getTownRoleConfigs() {
        return townRoleConfigs;
    }

    public void setTownRoleConfigs(Set<TownRoleConfig> townRoleConfigs) {
        this.townRoleConfigs = townRoleConfigs;
    }

    public Set<NationRoleConfig> getNationRoleConfigs() {
        return nationRoleConfigs;
    }

    public void setNationRoleConfigs(Set<NationRoleConfig> nationRoleConfigs) {
        this.nationRoleConfigs = nationRoleConfigs;
    }
}
