package com.atherys.towns.config;

import java.util.HashSet;

public class RankConfigBuilder {

    private RankConfig rankConfig;

    public RankConfigBuilder() {
        rankConfig = new RankConfig();
        rankConfig.PERMISSIONS = new HashSet<>();
        rankConfig.ACTIONS = new HashSet<>();
    }

    public RankConfigBuilder id(String id) {
        rankConfig.ID = id;
        return this;
    }

    public RankConfigBuilder name(String name) {
        rankConfig.NAME = name;
        return this;
    }

    public RankConfigBuilder permission(String commandPermission) {
        rankConfig.PERMISSIONS.add(commandPermission);
        return this;
    }

    public RankConfigBuilder action(String actionPermission) {
        rankConfig.ACTIONS.add(actionPermission);
        return this;
    }

    public RankConfig build() {
        return rankConfig;
    }
}
