package com.atherys.towns.api.plot.flag;

import org.spongepowered.api.CatalogType;

/**
 * Represents an action to be performed by a player, not necessarily linked with a permission
 */
public interface Flag extends CatalogType {

    /**
     * Gets the extents which are applicable to this Flag
     * @return The array of extents applicable
     */
    Extent[] getPermittedExtents();

}
