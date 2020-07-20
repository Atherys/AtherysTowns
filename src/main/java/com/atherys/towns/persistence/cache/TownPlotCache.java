package com.atherys.towns.persistence.cache;

import com.atherys.core.db.cache.SimpleCache;
import com.atherys.towns.model.entity.TownPlot;
import com.atherys.towns.service.PlotService;
import com.flowpowered.math.vector.Vector2i;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TownPlotCache extends SimpleCache<TownPlot, Long> {

    private final Map<Vector2i, Set<TownPlot>> plotChunkCache = new HashMap<>();

    public Set<TownPlot> getPlotsOverlappingChunk(Vector2i chunkCoordinate) {
        return plotChunkCache.getOrDefault(chunkCoordinate, new HashSet<>());
    }

    @Override
    public void add(TownPlot plot) {
        super.add(plot);
        PlotService.getChunksOverlappedByPlot(plot).forEach(chunkCoordinate -> {
            // Get plots already assigned to this chunk
            Set<TownPlot> plots = plotChunkCache.get(chunkCoordinate);

            // If none exist, create a new collection
            if (plots == null) {
                plots = new HashSet<>();
            }

            // add the plot to the corresponding chunk
            plots.add(plot);
            plotChunkCache.put(chunkCoordinate, plots);
        });
    }

    @Override
    public void remove(TownPlot plot) {
        super.remove(plot);
        PlotService.getChunksOverlappedByPlot(plot).forEach(chunkCoordinate -> {
            Set<TownPlot> plots = plotChunkCache.get(chunkCoordinate);
            if (plots != null)
                plots.remove(plot);
        });
    }
}
