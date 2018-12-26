package com.atherys.towns.model;

import com.atherys.towns.api.permission.ContextHolder;
import com.atherys.towns.api.permission.PermissionContext;

public class Plot implements ContextHolder<Plot, Town> {
    @Override
    public PermissionContext getContext() {
        return null;
    }

    @Override
    public Town getParent() {
        return null;
    }
}
