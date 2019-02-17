package com.atherys.towns.persistence;

import com.atherys.core.db.CachedHibernateRepository;
import com.atherys.towns.entity.Plot;
import com.atherys.towns.entity.Town;
import com.atherys.towns.persistence.cache.TownsCache;
import com.flowpowered.math.vector.Vector2i;
import com.flowpowered.math.vector.Vector3i;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Singleton
public class PlotRepository extends CachedHibernateRepository<Plot, Long> {

    private TownsCache townsCache;

    // TODO:
    /**
     * +-------+-------+-------+
     * |       |       |       |
     * |   X-------X-------X   |
     * |   |   |       |   |   |
     * +---|---+-------+---|---+
     * |   |   |       |   |   |
     * |   X   |   X   |   X   |
     * |   |   |       |   |   |
     * +---|---+-------+---|---+
     * |   |   |       |   |   |
     * |   X-------X-------X---------- - - -  -  -
     * |       |       |   |   |
     * +-------+-------+---|---+
     * |
     * |          Other plot this way...
     * Both of these plots should be added to the map
     * |          under the coordinate of the lower-right chunk
     * <p>
     * |
     * <p>
     * The inner rectangle is a Plot.
     * The grid behind it represents world chunks.
     * <p>
     * The X's represent possible positions for the testPoint, if the north-east and south-west corners of the plot are
     * located exactly at their chunk's midpoint.
     * <p>
     * Map<Vector2i,Set<Plot>> performanceCache; // The key is the chunk location, the value is all plots which cover this chunk
     */
    private Map<Vector2i, Set<Plot>> performanceCache = new HashMap<>();

    @Inject
    protected PlotRepository(TownsCache townsCache) {
        super(Plot.class);
        super.cache = townsCache.getPlotCache();
        this.townsCache = townsCache;
    }

    public Collection<Plot> getPlotsIntersectingChunk(Vector3i chunkPosition) {
        return cache.getAll(); // TODO: Create query which tests for chunk intersections
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
