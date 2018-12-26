package com.atherys.towns.model;

import com.atherys.towns.api.permission.Contextual;
import com.atherys.towns.api.permission.PermissionContext;
import com.atherys.towns.api.permission.Permissions;

import javax.persistence.Entity;
import java.util.HashMap;
import java.util.Map;

@Entity
public class SimplePermissionContext implements PermissionContext {

    private Map<Contextual, Permissions> permissions = new HashMap<>();

    public SimplePermissionContext() {
    }

    @Override
    public <T extends Contextual> boolean isPermitted(T contextual, int permissions) {
        return false;
    }

    @Override
    public <T extends Contextual> void setPermissions(T contextual, int pemissions) {

    }

    @Override
    public <T extends Contextual> void removePermissions(T contextual) {

    }
}
