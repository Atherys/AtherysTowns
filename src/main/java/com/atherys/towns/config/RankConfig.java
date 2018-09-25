package com.atherys.towns.config;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.Set;

@ConfigSerializable
public class RankConfig {

    @Setting("id")
    public String ID;

    @Setting("name")
    public String NAME;

    @Setting("permissions")
    public Set<String> PERMISSIONS;

    @Setting("actions")
    public Set<String> ACTIONS;

    public static RankConfigBuilder builder() {
        return new RankConfigBuilder();
    }

}
