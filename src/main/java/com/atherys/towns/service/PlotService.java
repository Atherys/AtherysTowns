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
        // TODO
        return false;
    }

    public boolean plotsBorder(Plot plotA, Plot plotB) {
        // TODO
        return false;
    }
}
