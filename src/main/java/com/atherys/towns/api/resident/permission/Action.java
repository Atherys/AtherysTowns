package com.atherys.towns.api.resident.permission;

import org.spongepowered.api.CatalogType;

/**
 * An action is a wrapper for a permission string which is to be used for permission checking within the Towns plugin
 */
public interface Action extends CatalogType {

    /**
     * Retrieves the permission string behind this Action
     *
     * @return The permission string
     */
    String getPermission();

}
