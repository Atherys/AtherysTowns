package com.atherys.towns.config;

import com.atherys.towns.api.permission.town.TownPermission;
import com.atherys.towns.api.permission.world.WorldPermission;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.HashSet;
import java.util.Set;

@ConfigSerializable
public class TownRoleConfig {

    @Setting("short-name")
    private String shortName;

    @Setting("name")
    private String name;

    @Setting("town-permissions")
    private Set<TownPermission> townPermissions = new HashSet<>();

    @Setting("world-permissions")
    private Set<WorldPermission> worldPermissions = new HashSet<>();

    public TownRoleConfig() {
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<TownPermission> getTownPermissions() {
        return townPermissions;
    }

    public void setTownPermissions(Set<TownPermission> townPermissions) {
        this.townPermissions = townPermissions;
    }

    public Set<WorldPermission> getWorldPermissions() {
        return worldPermissions;
    }

    public void setWorldPermissions(Set<WorldPermission> worldPermissions) {
        this.worldPermissions = worldPermissions;
    }
}
