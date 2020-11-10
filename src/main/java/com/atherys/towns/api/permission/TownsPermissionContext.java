package com.atherys.towns.api.permission;

import com.atherys.towns.api.permission.world.WorldPermissions;
import org.spongepowered.api.CatalogType;
import org.spongepowered.api.util.annotation.CatalogedBy;

import java.util.Objects;

@CatalogedBy(TownsPermissionContexts.class)
public class TownsPermissionContext implements CatalogType {

    private String id;

    private String commandElementName;

    private String name;

    public TownsPermissionContext(String id, String commandElementName, String name) {
        this.id = id;
        this.commandElementName = commandElementName;
        this.name = name;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    public String getCommandElementName() {
        return commandElementName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TownsPermissionContext that = (TownsPermissionContext) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
