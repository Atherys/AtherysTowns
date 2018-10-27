package com.atherys.towns.model.permission;

import com.google.common.base.Preconditions;
import org.spongepowered.api.util.Identifiable;

/**
 * Created by NeumimTo on 27.10.2018.
 */
public interface PermissionContext extends Identifiable {

    enum Type {
        PLOT,
        TOWN,
        NATION
    }

    Type getPermissionContext();

    default String getPermissionNodeForAction(ResidentAction residentAction) {
        Preconditions.checkArgument(!residentAction.validForContext(getPermissionContext()), residentAction + " Not valid for" +
                getPermissionContext() + "Context");

        return residentAction.getPermission(this);
    }
}
