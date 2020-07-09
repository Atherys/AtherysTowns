package com.atherys.towns.config;

import com.atherys.towns.api.permission.nation.NationPermissions;
import com.google.common.collect.ImmutableSet;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.HashMap;
import java.util.Map;

@ConfigSerializable
public class NationConfig {

    @Setting("max-nation-name-size")
    public int MAX_NATION_NAME_LENGTH = 25;

    @Setting("nation-leader-role")
    public String LEADER_ROLE = "leader";

    @Setting("nation-default-role")
    public String DEFAULT_ROLE = "citizen";

    @Setting("roles")
    public Map<String, NationRoleConfig> ROLES = new HashMap<>();

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
                NationPermissions.CHAT,
                NationPermissions.ADD_ALLY,
                NationPermissions.ADD_ENEMY,
                NationPermissions.ADD_NEUTRAL
        ));

        NationRoleConfig nationMember = new NationRoleConfig();
        nationMember.setName("Citizen");
        nationMember.setTownPermissions(ImmutableSet.of(
                NationPermissions.DEPOSIT_INTO_BANK,
                NationPermissions.CHAT
        ));

        ROLES.put("leader", nationLeader);
        ROLES.put("citizen", nationMember);
    }

}
