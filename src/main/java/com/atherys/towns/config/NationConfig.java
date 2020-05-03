package com.atherys.towns.config;

import com.atherys.towns.api.permission.nation.NationPermissions;
import com.atherys.towns.chat.NationMessageChannel;
import com.google.common.collect.ImmutableSet;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.text.Text;

import java.util.*;

@ConfigSerializable
public class NationConfig {

    @Setting("id")
    private String id = "nation-id";

    @Setting("name")
    private Text name = Text.of("Nation Name");

    @Setting("description")
    private Text description = Text.of("Example Description");

    @Setting("leader-uuid")
    private UUID leaderUuid = null;

    @Setting("capital-id")
    private Long capitalId = -1L;

    @Setting("freely-joinable")
    private boolean freelyJoinable = true;

    @Setting("tax")
    private double tax = 0.2;

    @Setting("bank")
    private UUID bank = UUID.randomUUID();

    @Setting("nation-leader-role")
    private String nationLeaderRole = "leader";

    @Setting("nation-default-role")
    private String nationDefaultRole = "citizen";

    @Setting("roles")
    private List<NationRoleConfig> roles = new ArrayList<>();
    {
        NationRoleConfig nationLeader = new NationRoleConfig();
        nationLeader.setId("leader");
        nationLeader.setName("Leader");
        nationLeader.setTownPermissions(ImmutableSet.of(
                NationPermissions.INVITE_TOWN,
                NationPermissions.KICK_TOWN,
                NationPermissions.SET_PERMISSION,
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
        nationLeader.setId("citizen");
        nationLeader.setName("Citizen");
        nationLeader.setTownPermissions(ImmutableSet.of(
                NationPermissions.DEPOSIT_INTO_BANK,
                NationPermissions.CHAT
        ));

        roles.add(nationLeader);
        roles.add(nationMember);
    }

    public NationConfig() {
    }

    public String getId() {
        return id;
    }

    public Text getName() {
        return name;
    }

    public Text getDescription() {
        return description;
    }

    public UUID getLeaderUuid() {
        return leaderUuid;
    }

    public Long getCapitalId() {
        return capitalId;
    }

    public boolean isFreelyJoinable() {
        return freelyJoinable;
    }

    public double getTax() {
        return tax;
    }

    public UUID getBank() {
        return bank;
    }

    public String getNationLeaderRole() {
        return nationLeaderRole;
    }

    public String getNationDefaultRole() {
        return nationDefaultRole;
    }

    public List<NationRoleConfig> getRoles() {
        return roles;
    }
}
