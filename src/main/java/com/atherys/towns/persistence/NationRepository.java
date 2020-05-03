package com.atherys.towns.persistence;

import com.atherys.core.db.CachedHibernateRepository;
import com.atherys.towns.config.NationConfig;
import com.atherys.towns.model.entity.Town;
import com.atherys.towns.persistence.cache.TownsCache;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.Collection;
import java.util.Optional;

@Singleton
public class NationRepository extends CachedHibernateRepository<NationConfig, Long> {

    private TownsCache townsCache;

    @Inject
    protected NationRepository(TownsCache townsCache) {
        super(NationConfig.class);
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

    public Collection<NationConfig> getAllNations() {
        return townsCache.getNationCache().getAll();
    }

    public Optional<NationConfig> findByName(String nationName) {
        return cache.findOne(n -> n.getName().equals(nationName));
    }
}
