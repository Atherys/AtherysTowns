package com.atherys.towns.api.permission;

public interface ContextHolder<T extends ContextHolder, P extends ContextHolder, C extends PermissionContext<T>> {

    C getContext();

    P getParent();

}
