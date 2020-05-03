package com.atherys.towns.api.permission.town;

import com.atherys.towns.api.permission.Permission;
import org.spongepowered.api.util.annotation.CatalogedBy;

@CatalogedBy(TownPermissions.class)
public class TownPermission extends Permission {
    TownPermission(String id, String name) {
        super("v" + id, name);
    }
}
