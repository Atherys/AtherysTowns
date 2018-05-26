package com.atherys.towns.plot;

import com.atherys.towns.api.plot.IPlot;
import com.atherys.towns.api.plot.PlotDefinition;
import com.atherys.towns.api.town.ITown;
import com.atherys.towns.views.PlotView;
import java.util.UUID;
import math.geom2d.Point2D;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class Plot implements IPlot {

    private UUID uuid;

    private PlotMeta meta = new PlotMeta();
    private PlotDefinition definition;

    private ITown town;

    Plot(UUID uuid) {
        this.uuid = uuid;
    }

    private Plot(PlotDefinition definition, ITown town, String name) {
        this.uuid = UUID.randomUUID();
        this.definition = definition;
        this.town = town;
        this.meta.setName(name);
    }

    public static Plot create(PlotDefinition define, ITown town, String name) {
        return new Plot(define, town, name);
    }

    public static PlotBuilder fromUUID(UUID uuid) {
        return new PlotBuilder(uuid);
    }

    public PlotBuilder builder() {
        return new PlotBuilder();
    }

    @Override
    public UUID getUUID() {
        return uuid;
    }

    @Override
    public PlotMeta getMeta() {
        return meta;
    }

    @Override
    public ITown getTown() {
        return town;
    }

    protected void setTown(ITown town) {
        this.town = town;
    }

    public PlotDefinition getDefinition() {
        return definition;
    }

    public void setDefinition(PlotDefinition definition) {
        this.definition = definition;
    }

    @Override
    public boolean contains(World w, double x, double y) {
        return definition.getWorld().equals(w) && definition.contains(x, y);
    }

    @Override
    public boolean contains(World w, Point2D point) {
        return definition.getWorld().equals(w) && definition.contains(point);
    }

    @Override
    public boolean contains(Location<World> loc) {
        return definition.contains(loc);
    }

    @Override
    public PlotView createView() {
        return new PlotView(this);
    }
}
