package com.atherys.towns.config;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.Set;

@ConfigSerializable
public class TownRankConfig {

    @Setting("name")
    public String NAME;

    @Setting("commands")
    public Set<String> COMMAND_PERMISSIONS;

    @Setting("actions")
    public Set<String> ACTION_PERMISSIONS;

}
