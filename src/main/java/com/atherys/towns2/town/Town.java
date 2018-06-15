package com.atherys.towns2.town;

import com.atherys.towns2.base.LocationContainer;
import com.atherys.towns2.nation.Nation;
import com.atherys.towns2.persistence.PlotManager;
import com.atherys.towns2.plot.Plot;
import com.atherys.towns2.plot.PlotFlag;
import org.spongepowered.api.util.Identifiable;
import org.spongepowered.api.util.Tristate;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import javax.annotation.Nonnull;
import java.util.*;

public class Town implements LocationContainer<World>, Identifiable {

    private UUID uuid;

    private Nation nation = null;
    private Location<World> spawnLocation;

    private List<Plot> plots = new ArrayList<>();

    private Map<PlotFlag,Tristate> defaultFlags;

    public Town() {
    }

    @Override
    public World getExtent() {
        return spawnLocation.getExtent();
    }

    @Override
    public boolean contains(Location<World> location) {
        for (Plot plot : plots) if (plot.contains(location)) return true;
        return false;
    }

    @Override
    public int getArea() {
        int area = 0;
        for (Plot plot : plots) area += plot.getArea();
        return area;
    }

    @Override
    @Nonnull
    public UUID getUniqueId() {
        return uuid;
    }

    public void claimPlot(Plot plot) {
        plot.setTown(this);
        defaultFlags.forEach((k,v) -> plot.getClaim().setPermission(k.getClaimFlag(), v, null, null));
    }

    public void updateAllFlags() {
        plots.forEach(plot -> defaultFlags.forEach((k, v) -> plot.getClaim().setPermission(k.getClaimFlag(), v, null, null)));
    }

    public void unclaimPlot(Plot plot) {
        this.plots.remove(plot);
        PlotManager.getInstance().removePlot(plot);
    }

    public Optional<Nation> getNation() {
        return Optional.ofNullable(nation);
    }

    public void setNation(Nation nation) {
        this.nation = nation;
    }
}
