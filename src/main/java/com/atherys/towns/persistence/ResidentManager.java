package com.atherys.towns.persistence;

import com.atherys.core.database.mongo.MorphiaDatabaseManager;
import com.atherys.towns.AtherysTowns;
import com.atherys.towns.model.Resident;

public class ResidentManager extends MorphiaDatabaseManager<Resident> {

    public ResidentManager() {
        super(AtherysTowns.getDatabase(), AtherysTowns.getLogger(), Resident.class);
    }

}
