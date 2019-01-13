package com.atherys.towns.persistence;

import com.atherys.core.db.AtherysRepository;
import com.atherys.towns.model.Town;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.slf4j.Logger;

import java.util.UUID;

@Singleton
public class TownRepository extends AtherysRepository<Town, UUID> {
    @Inject
    protected TownRepository(Logger logger) {
        super(Town.class, logger);
    }
}
