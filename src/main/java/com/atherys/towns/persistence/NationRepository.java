package com.atherys.towns.persistence;

import com.atherys.core.db.AtherysRepository;
import com.atherys.towns.AtherysTowns;
import com.atherys.towns.model.Nation;
import org.slf4j.Logger;

import java.util.UUID;

public class NationRepository extends AtherysRepository<Nation, UUID> {
    protected NationRepository() {
        super(Nation.class, AtherysTowns.getLogger());
    }
}
