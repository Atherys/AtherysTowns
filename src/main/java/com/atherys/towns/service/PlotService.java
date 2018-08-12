package com.atherys.towns.service;

import com.atherys.towns.db.PlotManager;
import com.google.inject.Inject;

public class PlotService {

    private PlotManager plotManager;

    @Inject
    public PlotService(PlotManager plotManager) {
        this.plotManager = plotManager;
    }



}
