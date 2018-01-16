package com.atherys.towns.base;

import com.atherys.core.database.api.DBObject;

public interface TownsObject extends DBObject {

    void setName(String name);

    String getName();

}
