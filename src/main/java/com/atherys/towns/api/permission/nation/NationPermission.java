package com.atherys.towns.api.permission.nation;

import com.atherys.towns.api.permission.Permission;

public class NationPermission extends Permission {
    NationPermission(String id, String name) {
        super("nation_" + id, name);
    }
}
