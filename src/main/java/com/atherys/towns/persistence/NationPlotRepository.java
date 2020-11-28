package com.atherys.towns.persistence;

import com.atherys.core.db.CachedHibernateRepository;
import com.atherys.towns.model.entity.NationPlot;
import com.atherys.towns.model.entity.Town;
import com.atherys.towns.persistence.cache.TownsCache;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.Collection;

@Singleton
public class NationPlotRepository extends CachedHibernateRepository<NationPlot, Long> {

    private TownsCache townsCache;

    @Inject
    public NationPlotRepository(TownsCache townsCache) {
        super(NationPlot.class);
        super.cache = townsCache.getNationPlotCache();
        this.townsCache = townsCache;
    }

    @Override
    public void initCache() {
        townsCache.getResidentCache().getAll().forEach(resident -> {
            Town town = resident.getTown();
            if (town != null && town.getNation() != null) {
                super.cache.addAll(town.getNation().getPlots());
            }
        });
    }

    public Collection<NationPlot> getAll() {
        return cache.getAll();
    }
}
