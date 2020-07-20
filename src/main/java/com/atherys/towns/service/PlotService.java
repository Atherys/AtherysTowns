package com.atherys.towns.service;

import com.atherys.towns.model.entity.*;
import com.atherys.towns.persistence.NationPlotRepository;
import com.atherys.towns.persistence.TownPlotRepository;
import com.atherys.towns.model.PlotSelection;
import com.atherys.towns.util.MathUtils;
import com.flowpowered.math.vector.Vector2d;
import com.flowpowered.math.vector.Vector2i;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Singleton
public class PlotService {

    public static final Text DEFAULT_TOWN_PLOT_NAME = Text.of("None");

    @Inject
    TownPlotRepository townPlotRepository;

    @Inject
    NationPlotRepository nationPlotRepository;

    @Inject
    ResidentService residentService;

    PlotService() {
    }

    public TownPlot createTownPlotFromSelection(PlotSelection selection) {
        TownPlot plot = new TownPlot();
        populatePlotFromSelection(plot, selection);
        plot.setName(DEFAULT_TOWN_PLOT_NAME);
        return plot;
    }

    public NationPlot createNationPlotFromSelection(PlotSelection selection) {
        NationPlot plot = new NationPlot();
        populatePlotFromSelection(plot, selection);
        return plot;
    }

    private void populatePlotFromSelection(Plot plot, PlotSelection selection) {
        Vector2d pA = selection.getPointA().getPosition().toVector2(true);
        Vector2d pB = selection.getPointB().getPosition().toVector2(true);

        Vector2d pNE;
        Vector2d pSW;

        //              NE
        //      +---+ pA
        //      |   |
        //   pB +---+
        // SW
        if (pA.getX() > pB.getX() && pA.getY() < pB.getY()) {
            pNE = pA;
            pSW = pB;

            //    +---+ pB
            //    |   |
            // pA +---+
        } else if (pA.getX() < pB.getX() && pA.getY() > pB.getY()) {
            pNE = pB;
            pSW = pA;

            // pB +---+
            //    |   |
            //    +---+ pA
        } else if (pA.getX() > pB.getX() && pA.getY() > pB.getY()) {
            pNE = Vector2d.from(pA.getX(), pB.getY());
            pSW = Vector2d.from(pB.getX(), pA.getY());

            // pA +---+
            //    |   |
            //    +---+ pB
        } else if (pA.getX() < pB.getX() && pA.getY() < pB.getY()) {
            pNE = Vector2d.from(pB.getX(), pA.getY());
            pSW = Vector2d.from(pA.getX(), pB.getY());

        } else {
            throw new IllegalArgumentException("Could not resolve south-west and north-east plot points.");
        }

        plot.setNorthEastCorner(new Vector2i(pNE.getFloorX(), pNE.getFloorY()));
        plot.setSouthWestCorner(new Vector2i(pSW.getFloorX(), pSW.getFloorY()));
    }


    public boolean plotsIntersect(Plot plotA, Plot plotB) {
        return MathUtils.overlaps(plotA.getSouthWestCorner(), plotA.getNorthEastCorner(), plotB.getSouthWestCorner(), plotB.getNorthEastCorner());
    }

    public boolean plotsBorder(Plot plotA, Plot plotB) {
        return MathUtils.borders(plotA.getSouthWestCorner(), plotA.getNorthEastCorner(), plotB.getSouthWestCorner(), plotB.getNorthEastCorner());
    }

    public boolean isLocationWithinPlot(Location<World> location, Plot plot) {
        return MathUtils.vectorXZFitsInRange(location.getBlockPosition(), plot.getSouthWestCorner(), plot.getNorthEastCorner());
    }

    public int getSmallestPlotSide(Plot plot) {
        Vector2i plotSize = getPlotSize(plot);
        return Math.min(plotSize.getX(), plotSize.getY());
    }

    public int getPlotArea(Plot plot) {
        Vector2i plotSize = getPlotSize(plot);
        return plotSize.getX() * plotSize.getY();
    }

    public Vector2i getPlotSize(Plot plot) {
        int sizeX = Math.abs(plot.getNorthEastCorner().getX() - plot.getSouthWestCorner().getX());
        int sizeY = Math.abs(plot.getNorthEastCorner().getY() - plot.getSouthWestCorner().getY());
        return new Vector2i(sizeX, sizeY);
    }

    public static Set<Vector2i> getChunksOverlappedByPlot(Plot plot) {
        Vector2i southWestCorner = plot.getSouthWestCorner();
        Vector2i northEastCorner = plot.getNorthEastCorner();

        Set<Vector2i> chunkCoordinates = new HashSet<>();

        // Calculate min corner and max corner chunks
        Vector2i southWestChunk = Vector2i.from(southWestCorner.getX() >> 4, southWestCorner.getY() >> 4); // South West Corner Chunk
        Vector2i northEastChunk = Vector2i.from(northEastCorner.getX() >> 4, northEastCorner.getY() >> 4); // North East Corner Chunk

        for (int x = southWestChunk.getX(); x <= northEastChunk.getX(); x++) {
            for (int y = northEastChunk.getY(); y <= southWestChunk.getY(); y++) {
                chunkCoordinates.add(Vector2i.from(x, y));
            }
        }
        return chunkCoordinates;
    }

    public boolean townPlotIntersectAnyOthers(TownPlot plot) {
        for (Vector2i chunkCoordinate : getChunksOverlappedByPlot(plot)) {
            for (TownPlot other : townPlotRepository.getPlotsIntersectingChunk(chunkCoordinate)) {
                if (plotsIntersect(plot, other)) {
                    return true;
                }
            }
        }
        return false;
    }

    public Optional<TownPlot> getTownPlotByLocation(Location<World> location) {
        for (TownPlot plot : townPlotRepository.getPlotsIntersectingChunk(location.getChunkPosition())) {
            if (isLocationWithinPlot(location, plot)) {
                return Optional.of(plot);
            }
        }

        return Optional.empty();
    }

    public void setTownPlotName(TownPlot plot, Text newName) {
        plot.setName(newName);
        townPlotRepository.saveOne(plot);
    }

    public void setTownPlotOwner(TownPlot plot, Resident owner) {
        plot.setOwner(owner);
        townPlotRepository.saveOne(plot);
    }

    public boolean townPlotBordersTown(Town town, TownPlot plot) {
        for (TownPlot townPlot : town.getPlots()) {
            if (plotsBorder(plot, townPlot)) {
                return true;
            }
        }
        return false;
    }

    public boolean townPlotIntersectsTown(Town town, TownPlot plot) {
        for (TownPlot townPlot : town.getPlots()) {
            if (!plotsIntersect(townPlot, plot)) {
                return true;
            }
        }

        return false;
    }
}
