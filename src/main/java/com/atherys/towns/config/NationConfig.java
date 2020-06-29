package com.atherys.towns.config;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.text.Text;

import java.util.UUID;

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

    @Setting("capital-name")
    private String capitalName = "";

    @Setting("freely-joinable")
    private boolean freelyJoinable = true;

    @Setting("tax")
    private double tax = 0.2;

    @Setting("bank")
    private UUID bank = UUID.randomUUID();

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

    public String getCapitalName() {
        return capitalName;
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
}
