package com.atherys.towns.service;

import com.atherys.towns.entity.Plot;
import com.atherys.towns.persistence.PlotRepository;
import com.atherys.towns.plot.PlotSelection;
import com.flowpowered.math.vector.Vector2d;
import com.flowpowered.math.vector.Vector3d;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

@Singleton
public class PlotService {

    @Inject
    PlotRepository plotRepository;

    PlotService() {
    }

    public Plot createPlotFromSelection(PlotSelection selection) {
        Plot plot = new Plot();

        /*

        // TODO: Figure out which point from the selection is going to be NW, and which SE.
        // Possible configurations are:
        // * NW - SE ( ideal )
        // * SE - NW ( Just need to flip )
        // * SW - NE ( Need to find NW and SE points )
        // * NE - SW ( Need to find NW and SE points )

      NW(-1,+1)     NE(+1,+1)
        +-----------+
        |     |     |
        |     |     |
        |-----+-----|
        |     |     |
        |     |     |
        +-----------+
      SW(-1,-1)     SE(+1,-1)

        1. Find midpoint of selection
        2. Find distance ( d ) between midpoint and either pointA or pointB of selection ( they should be equidistant )
        3. Since all points are equidistant from the center, use that to find blocks at NW and SE positions
        3.1. blockNW = ???
        3.2. blockSE = ???
        4. Once NW and SE blocks are found, get final NW and SE points as follows:
        4.1. pointNW = northwest-most point of blockNW
        4.2. pointSE = southeast-most point of blockSE

         */

        return plot;
    }

    public boolean plotsIntersect(Plot plotA, Plot plotB) {
        // TODO
        return false;
    }

    public boolean plotsBorder(Plot plotA, Plot plotB) {
        // TODO
        return false;
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

    public boolean plotIntersectsAnyOthers(Plot plot) {
        return plotRepository.cacheParallelStream().anyMatch(other -> plotsIntersect(plot, other));
    }
}
