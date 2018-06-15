package com.atherys.towns2.persistence;

import com.atherys.towns2.plot.Plot;

public class PlotManager {

    private static PlotManager instance = new PlotManager();

    private PlotManager() {

    }

    public void registerPlot(Plot plot) {

    }

    public void removePlot(Plot plot) {
        // TODO: delete plot claim
    }

    public static PlotManager getInstance() {
        return instance;
    }
}
