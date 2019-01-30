package com.atherys.towns.persistence;

import com.atherys.core.db.HibernateRepository;
import com.atherys.towns.entity.Resident;
import com.google.inject.Singleton;

import java.util.UUID;

@Singleton
public class ResidentRepository extends HibernateRepository<Resident, UUID> {
    protected ResidentRepository() {
        super(Resident.class);
    }
}
