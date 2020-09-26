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

    protected int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
}
