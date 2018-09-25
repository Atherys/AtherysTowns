package com.atherys.towns.model.permission;

import org.spongepowered.api.util.Identifiable;

import java.util.Objects;

public class PermissionPrototype<T extends Identifiable> {

    private String id;

    private String name;

    private String permission;

    public PermissionPrototype(String id, String name, String permission) {
        this.id = id;
        this.name = name;
        this.permission = permission;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPermission() {
        return permission;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PermissionPrototype<?> that = (PermissionPrototype<?>) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(name, that.name) &&
                Objects.equals(permission, that.permission);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, permission);
    }
}
