package com.atherys.towns.model.entity;

import com.atherys.core.db.Identifiable;
import com.atherys.towns.api.permission.world.WorldPermission;
import com.atherys.towns.persistence.converter.PermissionConverter;
import com.atherys.towns.persistence.converter.Vector2iConverter;
import com.atherys.towns.util.Rectangle;
import com.flowpowered.math.vector.Vector2i;

import javax.annotation.Nonnull;
import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@MappedSuperclass
public class Plot implements Rectangle, Identifiable<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Convert(converter = Vector2iConverter.class)
    private Vector2i swCorner;

    @Convert(converter = Vector2iConverter.class)
    private Vector2i neCorner;

    @ManyToOne(fetch = FetchType.EAGER)
    private Resident owner;


    @Convert(converter = PermissionConverter.class)
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<WorldPermission> friendPermissions = new HashSet<>();

    @Convert(converter = PermissionConverter.class)
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<WorldPermission> townPermissions = new HashSet<>();

    @Convert(converter = PermissionConverter.class)
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<WorldPermission> allyPermissions = new HashSet<>();

    @Convert(converter = PermissionConverter.class)
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<WorldPermission> enemyPermissions = new HashSet<>();

    @Convert(converter = PermissionConverter.class)
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<WorldPermission> neutralPermissions = new HashSet<>();

    @Version
    private int version;

    @Nonnull
    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Vector2i getSouthWestCorner() {
        return swCorner;
    }

    @Override
    public Vector2i getTopLeftCorner() {
        return swCorner;
    }

    public void setSouthWestCorner(Vector2i swCorner) {
        this.swCorner = swCorner;
    }

    @Override
    public void setTopLeftCorner(Vector2i point) {
        this.swCorner = point;
    }

    public Vector2i getNorthEastCorner() {
        return neCorner;
    }

    @Override
    public Vector2i getBottomRightCorner() {
        return neCorner;
    }

    public void setNorthEastCorner(Vector2i neCorner) {
        this.neCorner = neCorner;
    }

    @Override
    public void setBottomRightCorner(Vector2i point) {
        this.neCorner = point;
    }

    public Set<WorldPermission> getFriendPermissions() {
        return this.friendPermissions;
    }

    public void setFriendPermissions(Set<WorldPermission> friendPermissions) {
        this.friendPermissions = friendPermissions;
    }

    public Set<WorldPermission> getTownPermissions() {
        return this.townPermissions;
    }

    public void setTownPermissions(Set<WorldPermission> townPermissions) {
        this.townPermissions = townPermissions;
    }

    public Set<WorldPermission> getAllyPermissions() {
        return this.allyPermissions;
    }

    public void setAllyPermissions(Set<WorldPermission> allyPermissions) {
        this.allyPermissions = allyPermissions;
    }

    public Set<WorldPermission> getEnemyPermissions() {
        return this.enemyPermissions;
    }

    public void setEnemyPermissions(Set<WorldPermission> enemyPermissions) {
        this.enemyPermissions = enemyPermissions;
    }

    public Set<WorldPermission> getNeutralPermissions() {
        return this.neutralPermissions;
    }

    public void setNeutralPermissions(Set<WorldPermission> neutralPermissions) {
        this.neutralPermissions = neutralPermissions;
    }

    protected int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
}
