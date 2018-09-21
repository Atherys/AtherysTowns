package com.atherys.towns.persistence;

import com.atherys.core.database.mongo.MorphiaDatabaseManager;
import com.atherys.towns.AtherysTowns;
import com.atherys.towns.model.Resident;
import com.atherys.towns.model.Town;
import com.flowpowered.math.vector.Vector2d;
import java.util.Optional;

public class TownManager extends MorphiaDatabaseManager<Town> {

    public TownManager() {
        super(AtherysTowns.getDatabase(), AtherysTowns.getLogger(), Town.class);
    }

    public Optional<Town> createTownFromSelection(Vector2d A, Vector2d B, Resident mayor) {
        // TODO: Implement
        return Optional.empty();
    }
}
