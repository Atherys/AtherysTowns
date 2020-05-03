package com.atherys.towns.persistence;

import com.atherys.core.db.CachedHibernateRepository;
import com.atherys.towns.model.entity.Plot;
import com.atherys.towns.model.entity.Town;
import com.atherys.towns.persistence.cache.PlotCache;
import com.atherys.towns.persistence.cache.TownsCache;
import com.atherys.towns.util.MathUtils;
import com.flowpowered.math.vector.Vector3i;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.Collection;

@Singleton
public class PlotRepository extends CachedHibernateRepository<Plot, Long> {

    private TownsCache townsCache;

    private PlotCache plotCache;

    @Inject
    protected PlotRepository(TownsCache townsCache) {
        super(Plot.class);
        super.cache = townsCache.getPlotCache();
        this.plotCache = townsCache.getPlotCache();
        this.townsCache = townsCache;
    }

    public Collection<Plot> getPlotsIntersectingChunk(Vector3i chunkPosition) {
        return plotCache.getPlotsOverlappingChunk(MathUtils.vec3iToVec2i(chunkPosition));
    }

    public Collection<Plot> getAll() {
        return cache.getAll();
    }

    @Override
    public void initCache() {
        townsCache.getResidentCache().getAll().forEach(resident -> {
            Town town = resident.getTown();
            if (town != null) {
                cache.addAll(town.getPlots());
            }
        });
    }
}
