package com.atherys.towns.api.permission;

public interface ContextHolder<T extends ContextHolder, P extends ContextHolder> {

    PermissionContext getContext();

    P getParent();

}
