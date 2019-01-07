package com.atherys.towns.persistence;

import com.atherys.core.db.AtherysRepository;
import com.atherys.towns.model.Town;
import org.slf4j.Logger;

import java.util.UUID;

public class TownRepository extends AtherysRepository<Town, UUID> {
    protected TownRepository(Class<Town> persistable, Logger logger) {
        super(persistable, logger);
    }
}
