package com.atherys.towns2.town;

import com.atherys.towns2.base.LocationContainer;
import com.atherys.towns2.base.ResidentContainer;
import com.atherys.towns2.nation.Nation;
import com.atherys.towns2.persistence.PlotManager;
import com.atherys.towns2.plot.Plot;
import com.atherys.towns2.plot.PlotFlag;
import com.atherys.towns2.resident.Resident;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nonnull;
import org.spongepowered.api.util.Tristate;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class Town implements LocationContainer<World>, ResidentContainer {

    private UUID uuid;

    private Nation nation = null;
    private Location<World> spawnLocation;

    private Set<Plot> plots = new HashSet<>();
    private Set<Resident> residents = new HashSet<>();

    private Map<PlotFlag,Tristate> defaultFlags = new HashMap<>();

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

    public Optional<Nation> getNation() {
        return Optional.ofNullable(nation);
    }

    public void setNation(Nation nation) {
        this.nation = nation;
    }

    public Map<PlotFlag, Tristate> getDefaultFlags() {
        return defaultFlags;
    }

    public Location<World> getSpawnLocation() {
        return spawnLocation;
    }

    public Set<Plot> getPlots() {
        return plots;
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

    @Override
    public Set<Resident> getResidents() {
        return residents;
    }
}
