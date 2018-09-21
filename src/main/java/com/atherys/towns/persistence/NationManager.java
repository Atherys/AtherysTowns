package com.atherys.towns.persistence;

import com.atherys.core.database.mongo.MorphiaDatabaseManager;
import com.atherys.towns.AtherysTowns;
import com.atherys.towns.model.Nation;

public class NationManager extends MorphiaDatabaseManager<Nation> {

    public NationManager() {
        super(AtherysTowns.getDatabase(), AtherysTowns.getLogger(), Nation.class);
    }
}
