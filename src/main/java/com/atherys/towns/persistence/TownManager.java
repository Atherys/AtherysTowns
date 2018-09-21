package com.atherys.towns.persistence;

import com.atherys.core.database.mongo.MorphiaDatabaseManager;
import com.atherys.towns.AtherysTowns;
import com.atherys.towns.model.Plot;
import com.atherys.towns.model.Resident;
import com.atherys.towns.model.Town;
import java.util.Optional;

public class TownManager extends MorphiaDatabaseManager<Town> {

    public TownManager() {
        super(AtherysTowns.getDatabase(), AtherysTowns.getLogger(), Town.class);
    }

    public Optional<Town> createTownFromPlot(Plot plot, Resident mayor) {
        // TODO: Implement
        return Optional.empty();
    }
}
