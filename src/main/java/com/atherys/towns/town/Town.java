package com.atherys.towns.town;

import com.atherys.towns.AtherysTowns;
import com.atherys.towns.api.nation.INation;
import com.atherys.towns.api.plot.IPlot;
import com.atherys.towns.api.plot.PlotDefinition;
import com.atherys.towns.api.resident.IResident;
import com.atherys.towns.api.town.ITown;
import com.atherys.towns.events.plot.TownClaimPlotEvent;
import com.atherys.towns.events.plot.TownUnclaimPlotEvent;
import com.atherys.towns.plot.Plot;
import com.atherys.towns.resident.Resident;
import com.atherys.towns.views.TownView;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

//import com.atherys.towns.utils.old.Question;

public class Town implements ITown {

    private UUID uuid;

    private TownMeta meta = new TownMeta();

    private int maxSize;
    private Location<World> spawn;

    private INation nation;
    private List<IPlot> plots = new ArrayList<>();

    private IResident mayor;
    private List<IResident> residents = new ArrayList<>();

    protected Town(UUID uuid) {
        this.uuid = uuid;
    }

    private Town(PlotDefinition define, Resident mayor) {
        this.uuid = UUID.randomUUID();

        this.spawn = mayor.asUser().get().getPlayer().get().getLocation();

        Plot homePlot = Plot.create(define, this, "Home");
        getPlots().add(homePlot);

        mayor.setTown(this);
    }

    public static TownBuilder fromUUID(UUID uuid) {
        return new TownBuilder(uuid);
    }

    public static TownBuilder builder() {
        return new TownBuilder();
    }

    @Override
    public UUID getUUID() {
        return uuid;
    }

    @Override
    public TownMeta getMeta() {
        return meta;
    }

    @Override
    public boolean contains(World w, double x, double y) {
        for (IPlot p : getPlots()) {
            if (p.contains(w, x, y)) {
                return true;
            }
        }
        return false;
    }

    public int getSize() {
        int area = 0;
        for (IPlot p : getPlots()) {
            area += p.getDefinition().area();
        }
        return area;
    }

    @Override
    public Location<World> getSpawnLocation() {
        return this.spawn;
    }

    @Override
    public void setSpawnLocation(Location<World> location) {
        this.spawn = location;
    }

    public List<IResident> getResidents() {
        return residents;
    }

    public List<IPlot> getPlots() {
        return plots;
    }

    @Override
    public TownView createView() {
        return new TownView(this);
    }

    @Override
    public Optional<INation> getNation() {
        return Optional.ofNullable(nation);
    }

    @Override
    public void setNation(INation nation) {
        this.nation = nation;
    }

    @Override
    public IResident getMayor() {
        return mayor;
    }

    @Override
    public void setMayor(IResident resident) {
        this.mayor = resident;
    }

    @Override
    public void addPlot(PlotDefinition plotDefinition) {
        Plot plot = Plot.create(plotDefinition, this, null);
        getPlots().add(plot);

        Sponge.getEventManager().post(new TownClaimPlotEvent(plot));
    }

    @Override
    public void removePlot(IPlot plot) {
        if ( getPlots().contains(plot) ) {
            getPlots().remove(plot);
            Sponge.getEventManager().post(new TownUnclaimPlotEvent(plot));
        }
    }

    @Override
    public int getMaximumSize() {
        return maxSize;
    }

    @Override
    public void setMaximumSize(int maxSize) {
        this.maxSize = maxSize;
    }

    @Override
    public Optional<UniqueAccount> getBankAccount() {
        return AtherysTowns.getInstance().getEconomyService().flatMap(service -> service.getOrCreateAccount(getUUID()));
    }
}
