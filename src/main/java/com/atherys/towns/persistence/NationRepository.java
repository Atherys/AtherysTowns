package com.atherys.towns.persistence;

import com.atherys.core.db.AtherysRepository;
import com.atherys.towns.model.Nation;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import java.util.UUID;

@Singleton
public class NationRepository extends AtherysRepository<Nation, UUID> {
    @Inject
    protected NationRepository(Logger logger) {
        super(Nation.class, logger);
    }
}
