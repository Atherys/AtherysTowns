package com.atherys.towns.config;

import com.atherys.towns.api.permission.Permission;
import com.atherys.towns.api.permission.world.WorldPermission;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.HashSet;
import java.util.Set;

@ConfigSerializable
public class TownRoleConfig {

    @Setting("name")
    private String name;

    @Setting("town-permissions")
    private Set<Permission> townPermissions = new HashSet<>();

    @Setting("world-permissions")
    private Set<WorldPermission> worldPermissions = new HashSet<>();

    public TownRoleConfig() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Permission> getTownPermissions() {
        return townPermissions;
    }

    public void setTownPermissions(Set<Permission> townPermissions) {
        this.townPermissions = townPermissions;
    }

    public Set<WorldPermission> getWorldPermissions() {
        return worldPermissions;
    }

    public void setWorldPermissions(Set<WorldPermission> worldPermissions) {
        this.worldPermissions = worldPermissions;
    }
}
