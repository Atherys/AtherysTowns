package com.atherys.towns.api;

import com.atherys.core.database.api.DBObject;

/**
 * An extention of the DBObject class which introduces the mandatory requirement for all
 * Towns-related objects to also have a name ( which is not guaranteed to be unique ).
 */
public interface TownsObject extends DBObject {

    /**
     * Retrieve the name of this object
     *
     * @return The name
     */
    String getName();

    /**
     * Set the name of this object
     *
     * @param name The new name
     */
    void setName(String name);

}
