package com.atherys.towns.api.resident.permission;

import org.spongepowered.api.CatalogType;

import java.util.List;
import java.util.Optional;

/**
 * A rank represents a collection of actions. Ranks may have parents.
 */
public interface Rank extends CatalogType {

    /**
     * Retrieves the parent Rank of this rank
     *
     * @return An optional containing the parent rank. May be empty, if there is no parent,
     * meaning this Rank is the highest in its hierarchy
     */
    Optional<Rank> getParent();

    List<? extends Action> getActions();

}
