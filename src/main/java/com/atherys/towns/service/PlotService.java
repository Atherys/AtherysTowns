package com.atherys.towns.service;

import com.atherys.towns.entity.Plot;
import com.atherys.towns.persistence.PlotRepository;
import com.atherys.towns.plot.PlotSelection;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class PlotService {

    @Inject
    PlotRepository plotRepository;

    PlotService() {
    }

    public Plot createPlotFromSelection(PlotSelection selection) {
        // TODO
        return null;
    }

    public boolean plotsIntersect(Plot plotA, Plot plotB) {
        return (plotA.getNorthWestCorner().getFloorX() < plotB.getSouthEastCorner().getFloorX() &&
                plotA.getSouthEastCorner().getFloorX() > plotB.getNorthWestCorner().getFloorX() &&
                plotA.getNorthWestCorner().getFloorY() > plotB.getSouthEastCorner().getFloorY() &&
                plotA.getSouthEastCorner().getFloorY() < plotB.getNorthWestCorner().getFloorY());
    }

    public boolean plotsBorder(Plot plotA, Plot plotB) {
        return !plotsIntersect(plotA, plotB) &&
               (Math.abs(plotA.getNorthWestCorner().getFloorY() - plotB.getSouthEastCorner().getFloorY()) == 1 ||
                Math.abs(plotA.getSouthEastCorner().getFloorY() - plotB.getNorthWestCorner().getFloorY()) == 1 ||
                Math.abs(plotA.getNorthWestCorner().getFloorX() - plotB.getSouthEastCorner().getFloorX()) == 1 ||
                Math.abs(plotA.getSouthEastCorner().getFloorX() - plotB.getNorthWestCorner().getFloorX()) == 1);
    }
}
