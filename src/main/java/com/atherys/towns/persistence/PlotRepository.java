package com.atherys.towns.persistence;

import com.atherys.core.db.AtherysRepository;
import com.atherys.towns.entity.Plot;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.slf4j.Logger;

import java.util.UUID;

@Singleton
public class PlotRepository extends AtherysRepository<Plot, UUID> {
    @Inject
    protected PlotRepository(Logger logger) {
        super(Plot.class, logger);
    }
}
