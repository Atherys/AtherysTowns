package com.atherys.towns.entity;

import com.atherys.towns.api.permission.nation.NationPermission;
import com.atherys.towns.api.permission.role.Role;
import com.atherys.towns.api.permission.town.TownPermission;
import com.atherys.towns.api.permission.world.WorldPermission;
import com.atherys.towns.persistence.converter.PermissionConverter;

import javax.annotation.Nonnull;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Objects;
import java.util.Set;

@Entity
public class TownRole implements Role<TownPermission> {

    @Id
    public long id;

    private String shortName;

    private String name;

    @Convert(converter = PermissionConverter.class)
    private Set<TownPermission> permissions;

    @Convert(converter = PermissionConverter.class)
    private Set<WorldPermission> worldPermissions;

    public TownRole() {
    }

    @Nonnull
    @Override
    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Set<TownPermission> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<TownPermission> permissions) {
        this.permissions = permissions;
    }

    public Set<WorldPermission> getWorldPermissions() {
        return worldPermissions;
    }

    public void setWorldPermissions(Set<WorldPermission> worldPermissions) {
        this.worldPermissions = worldPermissions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TownRole townRole = (TownRole) o;
        return getId() == townRole.getId() &&
                Objects.equals(getShortName(), townRole.getShortName()) &&
                Objects.equals(getName(), townRole.getName()) &&
                Objects.equals(getPermissions(), townRole.getPermissions());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getShortName(), getName(), getPermissions());
    }
}
