package com.atherys.towns.service;

import com.atherys.towns.model.entity.Plot;
import com.atherys.towns.model.entity.Resident;
import com.atherys.towns.model.entity.Town;
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

    @Inject
    ResidentService residentService;

    PlotService() {
    }

    public Plot createPlotFromSelection(PlotSelection selection) {
        Plot plot = new Plot();

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
        return MathUtils.vectorXZFitsInRange(location.getBlockPosition(), plot.getSouthWestCorner(), plot.getNorthEastCorner());
    }

    public boolean plotIntersectsAnyOthers(Plot plot) {
        return plotRepository.getAll().stream().anyMatch(other -> plotsIntersect(plot, other));
    }

    public Optional<Plot> getPlotByLocation(Location<World> location) {
        for (Plot plot : plotRepository.getPlotsIntersectingChunk(location.getChunkPosition())) {
            if (isLocationWithinPlot(location, plot)) {
                return Optional.of(plot);
            }
        }

        return Optional.empty();
    }

    public void setPlotName(Plot plot, Text newName) {
        plot.setName(newName);
        plotRepository.saveOne(plot);
    }

    public void setPlotOwner(Plot plot, Resident owner) {
        plot.setOwner(owner);
        plotRepository.saveOne(plot);
    }

    public boolean plotBordersTown(Town town, Plot plot) {
        for (Plot townPlot : town.getPlots()) {
            if (plotsBorder(plot, townPlot)) {
                return true;
            }
        }
        return false;
    }

    public boolean plotIntersectsTown(Town town, Plot plot) {
        for (Plot townPlot : town.getPlots()) {
            if (!plotsIntersect(townPlot, plot)) {
                return true;
            }
        }

        return false;
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

    public Optional<Plot> getClosestPlot(Plot plot) {
        int centerX = (plot.getNorthEastCorner().getX() + plot.getSouthWestCorner().getX()) / 2;
        int centerZ = (plot.getNorthEastCorner().getY() + plot.getSouthWestCorner().getY()) / 2;

        Plot closest = null;
        double distance = 0;
        for (Plot p : plotRepository.getAll()) {
            if (p.equals(plot)) {
                continue;
            }

            double newDistance = MathUtils.getDistanceToPlotSquared(Vector2i.from(centerX, centerZ), p.getNorthEastCorner(), p.getSouthWestCorner());
            if (closest == null || newDistance < distance) {
                closest = p;
                distance = newDistance;
            }
        }

        return Optional.ofNullable(closest);
    }
}
