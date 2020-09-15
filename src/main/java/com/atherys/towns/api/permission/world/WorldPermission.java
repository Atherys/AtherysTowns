package com.atherys.towns.api.permission.world;

import com.atherys.towns.api.permission.Permission;
import org.spongepowered.api.util.annotation.CatalogedBy;

@CatalogedBy(WorldPermissions.class)
public class WorldPermission extends Permission {
    WorldPermission(String id, String name) {
        super(id, name);
    }
}
