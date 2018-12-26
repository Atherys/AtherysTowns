package com.atherys.towns.api.permission;

public interface PermissionContext {

    <T extends Contextual> boolean isPermitted(T contextual, int permissions);

    <T extends Contextual> void setPermissions(T contextual, int pemissions);

    <T extends Contextual> void removePermissions(T contextual);

}
