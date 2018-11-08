package com.atherys.towns.persistence;

import com.atherys.core.database.mongo.MorphiaDatabaseManager;
import com.atherys.towns.AtherysTowns;
import com.atherys.towns.model.Plot;
import com.atherys.towns.model.Town;
import com.flowpowered.math.vector.Vector2d;
import com.flowpowered.math.vector.Vector2i;
import com.flowpowered.math.vector.Vector3i;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import org.spongepowered.api.world.Chunk;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.*;

public class PlotManager extends MorphiaDatabaseManager<Plot> {

    private static PlotManager instance = new PlotManager();

    //99% of time we are going to call get()
    //see https://github.com/austinv11/Long-Map-Benchmarks
    //trove is not part a of minecraft env, these libraries are few megabites each, so we stick to fastutils which is already on the classpath somewhere
    //How to implement additional worlds? Maybe each world will have its own manager class
    //
    private Long2ObjectOpenHashMap<Set<Plot>> plotCache = new Long2ObjectOpenHashMap<>();

    private PlotManager() {
        super(AtherysTowns.getDatabase(), AtherysTowns.getLogger(), Plot.class);
    }


    @Override
    public void loadAll() {
        super.loadAll();
        Map<UUID, Plot> cache = getCache();
        for (Plot plot : cache.values()) {
            addPlot(plot);
        }
    }

    private void addPlot(Plot plot) {
        Vector2d max = plot.getMax();
        Vector2d min = plot.getMin();

        int xmin = Math.min(max.getFloorX(), min.getFloorX());
        int xmax = Math.max(max.getFloorX(), min.getFloorX());

        int zmin = Math.min(max.getFloorY(), min.getFloorY());
        int zmax = Math.max(max.getFloorY(), min.getFloorY());
        Set<Long> hashes = new HashSet<>();
        for (int x = xmin; x <= xmax; x+=16) {
            for (int z = zmin; z <= zmax; z+=16) {
                hashes.add(getPlotHash(x, z));
            }
        }
        addPlot(plot, hashes);
    }

    private void addPlot(Plot plot, Set<Long> chunks) {
        for (Long plotHash : chunks) {
            plotCache.compute((long)plotHash, (aLong, plots) -> {
                if (plots == null){
                    plots = new HashSet<>();
                }
                plots.add(plot);
                return plots;
            });
        }
    }

    public static PlotManager getInstance() {
        return instance;
    }

    public Optional<Plot> createPlot(Town town, Vector2d A, Vector2d B) {
        Plot result = null;

        if (A.compareTo(B) > 0) result = new Plot(town, A, B);

        if (A.compareTo(B) < 0) result = new Plot(town, B, A);

        return Optional.ofNullable(result);
    }

    public boolean plotContains(Plot plot, Location<World> location) {
        boolean sameWorld = plot.getWorld().equals(location.getExtent());
        if (!sameWorld) {
            return false;
        }
        boolean withinX = plot.getMin().getX() >= location.getPosition().getX() && plot.getMax().getX() <= location.getPosition().getX();
        boolean withinZ = plot.getMin().getY() >= location.getPosition().getZ() && plot.getMax().getY() <= location.getPosition().getZ();
        return withinX && withinZ;
    }

    public boolean plotsIntersect(Plot A, Plot B) {
        return A.getMin().getX() < B.getMax().getX() &&
                A.getMin().getY() < B.getMax().getY() &&
                A.getMax().getX() > B.getMin().getX() &&
                A.getMax().getY() > B.getMin().getY();
    }

    public Optional<Plot> getPlotByLocation(Location<World> location) {
        long plotHash = getPlotHash(location);
        Set<Plot> plots = plotCache.get(plotHash);
        if (plots == null) {
            return Optional.empty(); // = wilderness/an unclaimed territory whatever you call it
        }
        for (Plot plot : plots) {
            if (plotContains(plot, location)) {
                return Optional.of(plot);
            }
        }
        return Optional.empty();  // = wilderness/an unclaimed territory whatever you call it
    }

    /**
     * Pairing function to convert chunk coordinates to an integer
     * Taken from ChunkPos.asLong()
     *
     * So this plugin has no dependency on nms, and may remain functional on another spongeapi implementation such as LantenrPowered
     */
    private long getPlotHash(int x, int z)
    {
        return (long)x & 4294967295L | ((long)z & 4294967295L) << 32;
    }

    private long getPlotHash(Location<World> location) {
        return getPlotHash(location.getBlockX() >> 4, location.getBlockZ() >> 4); // shift by 4, 16 = 1, 32 = 2, ... = chunkpos from loc. no need to call getExtent which we would have to typecast then
    }

    private long getPlotHash(Vector3i chunkPos) {
        return getPlotHash(chunkPos.getX(), chunkPos.getZ());
    }

    private long getPlotHash(Vector2i chunkPos) {
        return getPlotHash(chunkPos.getX(), chunkPos.getY());
    }

    private long getPlotHash(Chunk chunk) {
        return getPlotHash(chunk.getPosition());
    }
}
