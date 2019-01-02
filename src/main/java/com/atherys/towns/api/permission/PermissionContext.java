package com.atherys.towns.api.permission;

public interface PermissionContext<H extends ContextHolder> {

    H getHolder();

    <C extends Contextual> boolean isPermitted(C contextual, int permissions);

    <C extends Contextual> void setPermissions(C contextual, int permissions);

    <C extends Contextual> void removePermissions(C contextual);

}
