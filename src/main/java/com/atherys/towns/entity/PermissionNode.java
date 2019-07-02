package com.atherys.towns.entity;

import com.atherys.core.db.Identifiable;
import com.atherys.towns.api.permission.Permission;

import javax.annotation.Nonnull;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import java.util.Objects;

@Entity
public class PermissionNode implements Identifiable<PermissionNodeId> {

    @EmbeddedId
    PermissionNodeId id;

    private boolean permitted;

    private int version;

    public PermissionNode() {
    }

    @Nonnull
    @Override
    public PermissionNodeId getId() {
        return id;
    }

    public void setId(PermissionNodeId id) {
        this.id = id;
    }

    public String getActorId() {
        return id.getActorId();
    }

    public String getSubjectId() {
        return id.getSubjectId();
    }

    public Permission getPermission() {
        return id.getPermission();
    }

    public boolean isPermitted() {
        return permitted;
    }

    public void setPermitted(boolean permitted) {
        this.permitted = permitted;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PermissionNode that = (PermissionNode) o;
        return permitted == that.permitted && that.id.equals(id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, permitted);
    }

    protected int getVersion() {
        return version;
    }

    protected void setVersion(int version) {
        this.version = version;
    }
}
