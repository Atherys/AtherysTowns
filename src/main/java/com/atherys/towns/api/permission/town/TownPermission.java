package com.atherys.towns.api.permission.town;

import com.atherys.towns.api.permission.Permission;

public class TownPermission extends Permission {
    TownPermission(String id, String name) {
        super("town_" + id, name);
    }
}
