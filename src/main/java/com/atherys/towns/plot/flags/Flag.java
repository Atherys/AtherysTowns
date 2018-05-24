package com.atherys.towns.plot.flags;

import org.spongepowered.api.CatalogType;
import org.spongepowered.api.util.annotation.CatalogedBy;

@CatalogedBy(Flags.class)
public class Flag implements CatalogType {

    private String id;
    private String name;

    private String permissionTemplate;
    private Extent[] extents;

    protected Flag(String id, String name, String permissionTemplate, Extent... extents) {
        this.id = id;
        this.name = name;
        this.permissionTemplate = permissionTemplate;
        this.extents = extents;

        FlagRegistry.getInstance().flags.put(id, this);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    public boolean checkExtent(Extent extent) {
        for (Extent e : extents) {
            if (e.equals(extent)) {
                return true;
            }
        }
        return false;
    }

    public String getAction() {
        return permissionTemplate;
    }

    @Override
    public String toString() {
        return getId();
    }
}
