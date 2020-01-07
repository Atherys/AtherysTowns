package com.atherys.towns.entity;

import com.atherys.towns.api.permission.nation.NationPermission;
import com.atherys.towns.api.permission.role.Role;
import com.atherys.towns.persistence.converter.PermissionConverter;

import javax.annotation.Nonnull;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Objects;
import java.util.Set;

@Entity
public class NationRole implements Role<NationPermission> {

    @Id
    private long id;

    private String shortName;

    private String name;

    @Convert(converter = PermissionConverter.class)
    private Set<NationPermission> permissions;

    public NationRole() {
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
    public Set<NationPermission> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<NationPermission> permissions) {
        this.permissions = permissions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NationRole that = (NationRole) o;
        return getId() == that.getId() &&
                Objects.equals(getShortName(), that.getShortName()) &&
                Objects.equals(getName(), that.getName()) &&
                Objects.equals(getPermissions(), that.getPermissions());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getShortName(), getName(), getPermissions());
    }
}
