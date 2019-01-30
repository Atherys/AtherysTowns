package com.atherys.towns.persistence;

import com.atherys.core.db.HibernateRepository;
import com.atherys.towns.entity.Nation;
import com.google.inject.Singleton;

import java.util.UUID;

@Singleton
public class NationRepository extends HibernateRepository<Nation, UUID> {
    protected NationRepository() {
        super(Nation.class);
    }
}
