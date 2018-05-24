package com.atherys.towns.plot;

import com.atherys.core.views.Viewable;
import com.atherys.towns.api.AbstractAreaObject;
import com.atherys.towns.api.Meta;
import com.atherys.towns.api.plot.IPlot;
import com.atherys.towns.api.plot.PlotDefinition;
import com.atherys.towns.managers.PlotManager;
import com.atherys.towns.plot.flags.Extent;
import com.atherys.towns.plot.flags.Flag;
import com.atherys.towns.resident.Resident;
import com.atherys.towns.town.Town;
import com.atherys.towns.views.PlotView;
import math.geom2d.Point2D;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.UUID;

public class Plot implements IPlot {

    private PlotDefinition definition;
    private PlotFlags flags;

    private String name;

    Plot(UUID uuid) {
        super(uuid);
    }

    private Plot(PlotDefinition definition, Town town, String name) {
        super(UUID.randomUUID());
        this.definition = definition;
        this.setParent(town);
        flags = PlotFlags.regular();
        this.name = name;
        PlotManager.getInstance().add(this);
        PlotManager.getInstance().save(this);
    }

    public static Plot create(PlotDefinition define, Town town, String name) {
        return new Plot(define, town, name);
    }

    public static PlotBuilder fromUUID(UUID uuid) {
        return new PlotBuilder(uuid);
    }

    public PlotBuilder builder() {
        return new PlotBuilder();
    }

    public boolean isResidentAllowedTo(Resident res, Flag flag) {
        return this.flags.isAllowed(res, flag, this);
    }

    public PlotDefinition getDefinition() {
        return definition;
    }

    public void setDefinition(PlotDefinition definition) {
        this.definition = definition;
    }

    public PlotFlags getFlags() {
        return flags;
    }

    public void setFlags(PlotFlags flags) {
        this.flags = flags.copy();
    }

    public void setFlag(Flag flag, Extent ext) {
        this.flags.set(flag, ext);
    }

    public Town getTown() {
        return super.getParent().get();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
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

    public void remove() {
        // TODO: When a plot is claimed and unclaimed, the plot is still stored in the database
        PlotManager.getInstance().remove(this);
    }

    @Override
    public PlotView createView() {
        return new PlotView(this);
    }

    @Override
    public UUID getUUID() {
        return null;
    }

    @Override
    public <T extends Meta> T getMeta() {
        return null;
    }

    @Override
    public void setExtent(com.atherys.towns.api.plot.flag.Flag flag, com.atherys.towns.api.plot.flag.Extent extent) {

    }

    @Override
    public void getExtent(com.atherys.towns.api.plot.flag.Flag flag) {

    }

    @Override
    public boolean isPermitted(Resident resident, com.atherys.towns.api.plot.flag.Flag flag) {
        return false;
    }

    @Override
    public void permit(Resident resident, com.atherys.towns.api.plot.flag.Flag flag) {

    }

    @Override
    public void restrict(Resident resident, com.atherys.towns.api.plot.flag.Flag flag) {

    }
}
