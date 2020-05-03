package com.atherys.towns.config;

import com.atherys.towns.api.permission.nation.NationPermission;
import com.atherys.towns.api.permission.town.TownPermission;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.HashSet;
import java.util.Set;

@ConfigSerializable
public class NationRoleConfig {

    @Setting("id")
    private String id;

    @Setting("name")
    private String name;

    @Setting("nation-permissions")
    private Set<NationPermission> townPermissions = new HashSet<>();

    public NationRoleConfig() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<NationPermission> getTownPermissions() {
        return townPermissions;
    }

    public void setTownPermissions(Set<NationPermission> townPermissions) {
        this.townPermissions = townPermissions;
    }
}
