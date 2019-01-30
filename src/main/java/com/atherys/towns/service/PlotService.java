package com.atherys.towns.service;

import com.atherys.towns.entity.Plot;
import com.atherys.towns.entity.Town;
import com.atherys.towns.persistence.PlotRepository;
import com.atherys.towns.plot.PlotSelection;
import com.atherys.towns.util.MathUtils;
import com.flowpowered.math.vector.Vector2d;
import com.flowpowered.math.vector.Vector2i;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;

@Singleton
public class PlotService {

    public static final Text DEFAULT_PLOT_NAME = Text.of("None");

    @Inject
    PlotRepository plotRepository;

    PlotService() {
    }

    public Plot createPlotFromSelection(PlotSelection selection) {
        Plot plot = new Plot();

        Vector2d pA = Vector2d.from(selection.getPointA().getPosition().getX(), selection.getPointA().getPosition().getZ());
        Vector2d pB = Vector2d.from(selection.getPointB().getPosition().getX(), selection.getPointB().getPosition().getZ());

        Vector2d pNE;
        Vector2d pSW;

        //    +---+ pA
        //    |   |
        // pB +---+
        if (pA.getX() > pB.getX() && pA.getY() > pB.getY()) {
            pNE = pA;
            pSW = pB;

            //    +---+ pB
            //    |   |
            // pA +---+
        } else if (pA.getX() < pB.getX() && pA.getY() < pB.getY()) {
            pNE = pB;
            pSW = pA;

            // pB +---+
            //    |   |
            //    +---+ pA
        } else if (pA.getX() > pB.getX() && pA.getY() < pB.getY()) {
            pNE = Vector2d.from(pA.getX(), pB.getY());
            pSW = Vector2d.from(pB.getX(), pA.getY());

            // pA +---+
            //    |   |
            //    +---+ pB
        } else if (pA.getX() < pB.getX() && pA.getY() > pB.getY()) {
            pNE = Vector2d.from(pB.getX(), pA.getY());
            pSW = Vector2d.from(pA.getX(), pB.getY());

        } else {
            throw new IllegalArgumentException("Could not resolve south-west and north-east plot points.");
        }

        plot.setNorthEastCorner(Vector2i.from((int) Math.ceil(pNE.getX()), (int) Math.ceil(pNE.getY())));
        plot.setSouthWestCorner(Vector2i.from(pSW.getFloorX(), pSW.getFloorY()));

        plot.setName(DEFAULT_PLOT_NAME);

        return plot;
    }

    public boolean plotsIntersect(Plot plotA, Plot plotB) {
        return MathUtils.overlaps(plotA.getSouthWestCorner(), plotA.getNorthEastCorner(), plotB.getSouthWestCorner(), plotB.getNorthEastCorner());
    }

    public boolean plotsBorder(Plot plotA, Plot plotB) {
        return MathUtils.borders(plotA.getSouthWestCorner(), plotA.getNorthEastCorner(), plotB.getSouthWestCorner(), plotB.getNorthEastCorner());
    }

    public boolean isLocationWithinPlot(Location<World> location, Plot plot) {
        return MathUtils.vectorXZFitsInRange(location.getPosition(), plot.getSouthWestCorner(), plot.getNorthEastCorner());
    }

    public boolean plotIntersectsAnyOthers(Plot plot) {
        return plotRepository.parallelStream().anyMatch(other -> plotsIntersect(plot, other));
    }

    public Optional<Plot> getPlotByLocation(Location<World> location) {
        for (Plot plot : plotRepository.getPlotsAtChunk(location.getChunkPosition())) {
            if (isLocationWithinPlot(location, plot)) return Optional.of(plot);
        }

        return Optional.empty();
    }

    public void setPlotName(Plot plot, Text newName) {
        plot.setName(newName);
        plotRepository.saveOne(plot);
    }

    public boolean plotBordersTown(Town town, Plot plot) {
        for (Plot townPlot : town.getPlots()) {
            if (!plotsBorder(townPlot, plot)) {
                return false;
            }
        }

        return true;
    }

    public boolean plotIntersectsTown(Town town, Plot plot) {
        for (Plot townPlot : town.getPlots()) {
            if (!plotsIntersect(townPlot, plot)) {
                return true;
            }
        }

        return false;
    }
}
