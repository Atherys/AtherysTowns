package com.atherys.towns.persistence;

import com.atherys.core.db.AtherysRepository;
import com.atherys.towns.model.Resident;
import org.slf4j.Logger;

import java.util.UUID;

public class ResidentRepository extends AtherysRepository<Resident, UUID> {
    protected ResidentRepository(Class<Resident> persistable, Logger logger) {
        super(persistable, logger);
    }
}
