package com.atherys.towns.persistence;

import com.atherys.core.db.AtherysRepository;
import com.atherys.towns.entity.Plot;
import com.atherys.towns.util.MathUtils;
import com.flowpowered.math.vector.Vector2i;
import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Singleton
public class PlotRepository extends AtherysRepository<Plot, UUID> {


    /**

    +-------+-------+-------+
    |       |       |       |
    |   X-------X-------X   |
    |   |   |       |   |   |
    +---|---+-------+---|---+
    |   |   |       |   |   |
    |   X   |   X   |   X   |
    |   |   |       |   |   |
    +---|---+-------+---|---+
    |   |   |       |   |   |
    |   X-------X-------X---------- - - -  -  -
    |       |       |   |   |
    +-------+-------+---|---+
                        |
                        |          Other plot this way...
                                   Both of these plots should be added to the map
                        |          under the coordinate of the lower-right chunk

                        |

    The inner rectangle is a Plot.
    The grid behind it represents world chunks.

    The X's represent possible positions for the testPoint, if the north-east and south-west corners of the plot are
    located exactly at their chunk's midpoint.

    Map<Vector2i,Set<Plot>> performanceCache; // The key is the chunk location, the value is all plots which cover this chunk

     */
    private Map<Vector2i, Set<Plot>> performanceCache = new HashMap<>();

    @Inject
    protected PlotRepository(Logger logger) {
        super(Plot.class, logger);
    }

    @Override
    public void cacheAll() {
        CriteriaBuilder builder = getCriteriaBuilder();
        CriteriaQuery<Plot> query = builder.createQuery(Plot.class);
        Root<Plot> variableRoot = query.from(Plot.class);

        query.select(variableRoot);

        asyncQueryMultiple(createQuery(query)).thenAccept(entities -> entities.forEach(this::cachePlot));
    }

    private void cachePlot(Plot entity) {
        entityManager.detach(entity);
        cache.put(entity.getId(), entity);

        Vector3i testPoint = Vector3i.from(entity.getSouthWestCorner().getX(), 0, entity.getSouthWestCorner().getY());

        // while the test point coordinates are still within X range of the plot
        while (MathUtils.fitsInRange(testPoint.getX(), entity.getSouthWestCorner().getX(), entity.getNorthEastCorner().getX())) {
            // while the test point coordinates are still within the Y range of the plot
            while (MathUtils.fitsInRange(testPoint.getY(), entity.getSouthWestCorner().getY(), entity.getNorthEastCorner().getY())) {
                // get the chunk coordinates of the current test point and add it to the cache
                Vector3i chunkCoords = Sponge.getServer().getChunkLayout().forceToChunk(testPoint);
                Vector2i chunkPos = Vector2i.from(chunkCoords.getX(), chunkCoords.getY());

                Set<Plot> plotSet;

                if ( performanceCache.containsKey(chunkPos) ) {
                    plotSet = performanceCache.get(chunkPos);
                } else {
                    plotSet = new HashSet<>();
                }

                plotSet.add(entity);
                performanceCache.put(chunkPos, plotSet);

                // Move the test point 1 chunk side length in the Z direction
                testPoint.add(0, 0, 16);
            }

            // Move the test point 1 chunk side length in the X direction
            testPoint.add(16, 0, 0);
        }
    }

    public Set<Plot> getPlotsAtChunk(Vector3i chunkPosition) {
        return performanceCache.get(Vector2i.from(chunkPosition.getX(), chunkPosition.getY()));
    }
}
