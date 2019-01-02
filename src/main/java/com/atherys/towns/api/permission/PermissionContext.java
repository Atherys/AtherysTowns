package com.atherys.towns.api.permission;

public interface PermissionContext<H extends ContextHolder> {

    H getHolder();

}
