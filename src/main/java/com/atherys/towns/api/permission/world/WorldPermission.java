package com.atherys.towns.api.permission.world;

import com.atherys.towns.api.permission.Permission;

public class WorldPermission extends Permission {
    WorldPermission(String id, String name) {
        super("world_" + id, name);
    }
}
