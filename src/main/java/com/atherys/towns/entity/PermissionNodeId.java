package com.atherys.towns.entity;

import com.atherys.towns.api.permission.Permission;
import com.atherys.towns.persistence.converter.PermissionConverter;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class PermissionNodeId implements Serializable {
    @Column(name = "actor_id")
    private String actorId;

    @Column(name = "subject_id")
    private String subjectId;

    @Convert(converter = PermissionConverter.class)
    private Permission permission;

    public PermissionNodeId() {
    }

    public PermissionNodeId(String actorId, String subjectId, Permission permission) {
        this.actorId = actorId;
        this.subjectId = subjectId;
        this.permission = permission;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PermissionNodeId)) return false;
        PermissionNodeId that = (PermissionNodeId) o;
        return actorId.equals(that.actorId) &&
                subjectId.equals(that.subjectId) &&
                permission.equals(that.permission);
    }

    @Override
    public int hashCode() {
        return Objects.hash(actorId, subjectId, permission);
    }
}
