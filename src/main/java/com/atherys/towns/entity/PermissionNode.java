package com.atherys.towns.entity;

import com.atherys.core.db.Identifiable;
import com.atherys.towns.api.permission.Permission;
import com.atherys.towns.persistence.converter.PermissionConverter;

import javax.annotation.Nonnull;
import javax.persistence.*;
import java.util.Objects;

@Entity
public class PermissionNode implements Identifiable<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "actor_id")
    private String actorId;

    @Column(name = "subject_id")
    private String subjectId;

    @Convert(converter = PermissionConverter.class)
    private Permission permission;

    private boolean permitted;

    private int version;

    public PermissionNode() {
    }

    @Nonnull
    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getActorId() {
        return actorId;
    }

    public void setActorId(String actorId) {
        this.actorId = actorId;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public Permission getPermission() {
        return permission;
    }

    public void setPermission(Permission permission) {
        this.permission = permission;
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
        return permitted == that.permitted &&
                id.equals(that.id) &&
                actorId.equals(that.actorId) &&
                subjectId.equals(that.subjectId) &&
                permission.equals(that.permission);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, actorId, subjectId, permission, permitted);
    }

    protected int getVersion() {
        return version;
    }

    protected void setVersion(int version) {
        this.version = version;
    }
}
