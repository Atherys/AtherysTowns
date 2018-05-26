package com.atherys.towns.plot;

import com.atherys.towns.api.plot.PlotDefinition;
import com.atherys.towns.managers.PlotManager;
import com.atherys.towns.town.Town;

import java.util.UUID;

public final class PlotBuilder {

    private Plot plot;

    PlotBuilder() {
        this.plot = new Plot(UUID.randomUUID());
    }

    PlotBuilder(UUID uuid) {
        this.plot = new Plot(uuid);
    }

    public PlotBuilder definition(PlotDefinition definition) {
        plot.setDefinition(definition);
        return this;
    }

    public PlotBuilder town(Town town) {
        plot.setTown(town);
        return this;
    }

    public PlotBuilder name(String name) {
        plot.setName(name);
        return this;
    }

    public Plot build() {
        PlotManager.getInstance().add(plot);
        return plot;
    }

}
