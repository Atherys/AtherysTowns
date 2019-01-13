package com.atherys.towns.persistence;

import com.atherys.core.db.AtherysRepository;
import com.atherys.towns.model.Resident;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.slf4j.Logger;

import java.util.UUID;

@Singleton
public class ResidentRepository extends AtherysRepository<Resident, UUID> {
    @Inject
    protected ResidentRepository(Logger logger) {
        super(Resident.class, logger);
    }
}
