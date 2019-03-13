package com.atherys.towns.persistence;

import com.atherys.core.db.CachedHibernateRepository;
import com.atherys.towns.entity.Resident;
import com.atherys.towns.persistence.cache.TownsCache;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.UUID;

@Singleton
public class ResidentRepository extends CachedHibernateRepository<Resident, UUID> {

    private TownsCache townsCache;

    @Inject
    protected ResidentRepository(TownsCache townsCache) {
        super(Resident.class);
        super.cache = townsCache.getResidentCache();
        this.townsCache = townsCache;
    }

}
