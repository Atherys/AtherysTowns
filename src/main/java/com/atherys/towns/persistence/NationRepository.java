package com.atherys.towns.persistence;

import com.atherys.core.db.CachedHibernateRepository;
import com.atherys.towns.model.entity.Nation;
import com.atherys.towns.model.entity.Town;
import com.atherys.towns.persistence.cache.TownsCache;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.Collection;
import java.util.Optional;

@Singleton
public class NationRepository extends CachedHibernateRepository<Nation, Long> {

    private TownsCache townsCache;

    @Inject
    protected NationRepository(TownsCache townsCache) {
        super(Nation.class);
        super.cache = townsCache.getNationCache();
        this.townsCache = townsCache;
    }

    @Override
    public void initCache() {
        townsCache.getResidentCache().getAll().forEach(resident -> {
            Town town = resident.getTown();
            if (town != null && town.getNation() != null) {
                super.cache.add(town.getNation());
            }
        });
    }

    public Collection<Nation> getAll() {
        return cache.getAll();
    }

    public Optional<Nation> findByName(String nationName) {
        return cache.findOne(n -> n.getName().equalsIgnoreCase(nationName));
    }
}