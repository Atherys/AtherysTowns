package com.atherys.towns.model.entity;

import com.atherys.towns.api.permission.TownsPermissionContext;
import com.atherys.towns.api.permission.world.WorldPermission;
import com.atherys.towns.persistence.converter.PermissionConverter;
import com.atherys.towns.persistence.converter.TownsPermissionContextConverter;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(schema = "atherystowns", name = "TownPlotPermission")
public class TownPlotPermission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Convert(converter = TownsPermissionContextConverter.class)
    private TownsPermissionContext context;

    @Convert(converter = PermissionConverter.class)
    private WorldPermission worldPermission;

    public TownPlotPermission() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TownsPermissionContext getContext() {
        return context;
    }

    public void setContext(TownsPermissionContext context) {
        this.context = context;
    }

    public WorldPermission getWorldPermission() {
        return worldPermission;
    }

    public void setWorldPermission(WorldPermission worldPermission) {
        this.worldPermission = worldPermission;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TownPlotPermission that = (TownPlotPermission) o;
        return Objects.equals(context, that.context) &&
                Objects.equals(worldPermission, that.worldPermission);
    }

    @Override
    public int hashCode() {
        return Objects.hash(context, worldPermission);
    }
}
