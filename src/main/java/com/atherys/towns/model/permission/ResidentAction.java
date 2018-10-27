package com.atherys.towns.model.permission;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;

/**
 * Created by NeumimTo on 27.10.2018.
 */
public class ResidentAction {

    private final String permission;
    private Set<PermissionContext.Type> validFor;

    public ResidentAction(String permission, PermissionContext.Type... validFor) {
        this.permission = permission;
        this.validFor = EnumSet.noneOf(PermissionContext.Type.class);
        this.validFor.addAll(Arrays.asList(validFor));
    }

    public String getRawPermission() {
        return permission;
    }

    public String getPermission(PermissionContext context) {
        return permission + "." + context.getPermissionContext().name().toLowerCase() + "." + context.getUniqueId();
    }


    public boolean validForContext(PermissionContext.Type type) {
        return validFor.contains(type);
    }

    @Override public String toString() {
        return "ResidentAction{" +
                "permission='" + permission + '\'' +
                '}';
    }
}
