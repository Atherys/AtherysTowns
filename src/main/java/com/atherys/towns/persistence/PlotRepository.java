package com.atherys.towns.persistence;

import com.atherys.core.db.AtherysRepository;
import com.atherys.towns.model.Plot;
import org.slf4j.Logger;

import java.util.UUID;

public class PlotRepository extends AtherysRepository<Plot, UUID> {
    protected PlotRepository(Class<Plot> persistable, Logger logger) {
        super(persistable, logger);
    }
}
