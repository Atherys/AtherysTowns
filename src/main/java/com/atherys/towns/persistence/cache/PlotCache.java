package com.atherys.towns.persistence.cache;

import com.atherys.core.db.cache.SimpleCache;
import com.atherys.towns.model.entity.Plot;
import com.flowpowered.math.vector.Vector2i;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PlotCache extends SimpleCache<Plot, Long> {

    private Map<Vector2i, Set<Plot>> performanceCache = new HashMap<>();

    public Set<Plot> getPlotsOverlappingChunk(Vector2i chunkCoordinate) {
        return performanceCache.getOrDefault(chunkCoordinate, new HashSet<>());
    }

    @Override
    public void add(Plot plot) {
        super.add(plot);
        getChunksOverlappedByPlot(plot).forEach(chunkCoordinate -> {
            // Get plots already assigned to this chunk
            Set<Plot> plots = performanceCache.get(chunkCoordinate);

            // If none exist, create a new collection
            if (plots == null) {
                plots = new HashSet<>();
            }

            // add the plot to the corresponding chunk
            plots.add(plot);
            performanceCache.put(chunkCoordinate, plots);
        });
    }

    private Set<Vector2i> getChunksOverlappedByPlot(Plot plot) {
        Vector2i southWestCorner = plot.getSouthWestCorner();
        Vector2i northEastCorner = plot.getNorthEastCorner();

        Set<Vector2i> chunkCoordinates = new HashSet<>();

        // Calculate min corner and max corner chunks
        Vector2i southWestChunk = Vector2i.from(southWestCorner.getX() >> 4, southWestCorner.getY() >> 4); // South West Corner Chunk
        Vector2i northEastChunk = Vector2i.from(northEastCorner.getX() >> 4, northEastCorner.getY() >> 4); // North East Corner Chunk

        for (int x = southWestChunk.getX(); x <= northEastChunk.getX(); x++) {
            for (int y = southWestChunk.getY(); y <= northEastChunk.getY(); y++) {
                chunkCoordinates.add(Vector2i.from(x, y));
            }
        }
        return chunkCoordinates;
    }
}
