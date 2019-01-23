package com.atherys.towns.service;

import com.atherys.towns.entity.Plot;
import com.atherys.towns.persistence.PlotRepository;
import com.atherys.towns.plot.PlotSelection;
import com.flowpowered.math.vector.Vector2d;
import com.flowpowered.math.vector.Vector2i;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

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

        Vector2d pNW;
        Vector2d pSE;

        //    +--+ pA
        //    |  |
        // pB +--+
        if (pA.getX() > pB.getX() && pA.getY() > pB.getY()) {
            pNW = Vector2d.from(pB.getX(), pA.getY());
            pSE = Vector2d.from(pA.getX(), pB.getY());

            //    +--+ pB
            //    |  |
            // pA +--+
        } else if (pA.getX() < pB.getX() && pA.getY() < pB.getY()) {
            pNW = Vector2d.from(pA.getX(), pB.getY());
            pSE = Vector2d.from(pB.getX(), pA.getY());

            // pB +--+
            //    |  |
            //    +--+ pA
        } else if (pA.getX() < pB.getX() && pA.getY() > pB.getY()) {
            pNW = pB;
            pSE = pA;

            // pA +--+
            //    |  |
            //    +--+ pB
        } else if (pA.getX() > pB.getX() && pA.getY() < pB.getY()) {
            pNW = pA;
            pSE = pB;
        } else {
            throw new IllegalArgumentException("Could not resolve north-west and south-east plot points.");
        }

        plot.setNorthWestCorner(Vector2i.from(pNW.getFloorX(), (int) Math.ceil(pNW.getY())));
        plot.setSouthEastCorner(Vector2i.from((int) Math.ceil(pSE.getX()), pSE.getFloorY()));

        plot.setName(DEFAULT_PLOT_NAME);

        return plot;
    }

    public boolean plotsIntersect(Plot plotA, Plot plotB) {
        return (plotA.getNorthWestCorner().getX() < plotB.getSouthEastCorner().getX() &&
                plotA.getSouthEastCorner().getX() > plotB.getNorthWestCorner().getX() &&
                plotA.getNorthWestCorner().getY() > plotB.getSouthEastCorner().getY() &&
                plotA.getSouthEastCorner().getY() < plotB.getNorthWestCorner().getY());
    }

    public boolean plotsBorder(Plot plotA, Plot plotB) {
        return !plotsIntersect(plotA, plotB) &&
                (Math.abs(plotA.getNorthWestCorner().getY() - plotB.getSouthEastCorner().getY()) == 1 ||
                        Math.abs(plotA.getSouthEastCorner().getY() - plotB.getNorthWestCorner().getY()) == 1 ||
                        Math.abs(plotA.getNorthWestCorner().getX() - plotB.getSouthEastCorner().getX()) == 1 ||
                        Math.abs(plotA.getSouthEastCorner().getX() - plotB.getNorthWestCorner().getX()) == 1);
    }

    public boolean isLocationWithinPlot(Location<World> location, Plot plot) {
        // if location and plot do not have same world uuids, return false
        if (!location.getExtent().getUniqueId().equals(plot.getTown().getWorld())) {
            return false;
        }

        // if location X does not fit between plot corners' X, return false
        if (plot.getNorthWestCorner().getX() > location.getPosition().getX() ||
                plot.getSouthEastCorner().getX() < location.getPosition().getX()) {
            return false;
        }

        // if location Y fits between plot corners' Y, return true
        return !(plot.getNorthWestCorner().getY() < location.getPosition().getX()) &&
                !(plot.getSouthEastCorner().getY() > location.getPosition().getZ());
    }

    public boolean plotSelectionIntersectsAnyPlots(PlotSelection selection) {
        return plotIntersectsAnyOthers(createPlotFromSelection(selection));
    }

    public boolean plotIntersectsAnyOthers(Plot plot) {
        return plotRepository.cacheParallelStream().anyMatch(other -> plotsIntersect(plot, other));
    }
}
