package com.atherys.towns.events.plot;

import com.atherys.towns.api.plot.IPlot;

public class TownUnclaimPlotEvent extends PlotEvent {
    public TownUnclaimPlotEvent(IPlot plot) {
        super(plot);
    }
}
