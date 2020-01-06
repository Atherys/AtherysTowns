package com.atherys.towns.persistence;

import com.atherys.core.db.CachedHibernateRepository;
import com.atherys.towns.entity.Town;
import com.atherys.towns.persistence.cache.TownsCache;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.Collection;
import java.util.Optional;

@Singleton
public class TownRepository extends CachedHibernateRepository<Town, Long> {

    @Inject
    private TownsCache townsCache;

    @Inject
    protected TownRepository(TownsCache townsCache) {
        super(Town.class);
        super.cache = townsCache.getTownCache();
        this.townsCache = townsCache;
    }

    public Optional<Town> findByName(String townName) {
        return cache.findOne(t -> t.getName().equals(townName));
    }

    @Override
    public void initCache() {
        townsCache.getResidentCache().getAll().forEach(resident -> {
            Town town = resident.getTown();
            if (town != null) {
                cache.add(town);
            }
        });
    }

    public Collection<Town> getAll() {
        return townsCache.getTownCache().getAll();
    }
}
