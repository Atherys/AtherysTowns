package com.atherys.towns.api.resident.permission;

/**
 * A rank holder represents an object which may hold a rank, and can be checked for permissions
 */
public interface RankHolder {

    /**
     * Checks if this RankHolder is permitted to carry out the given action within its own context
     *
     * @param action The action to be performed
     * @return Whether or not the RankHolder is permitted
     */
    <T extends Action> boolean isPermitted(T action);

}
