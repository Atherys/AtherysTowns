package com.atherys.towns.api.town;

import com.atherys.core.database.api.DBObject;
import com.atherys.core.views.Viewable;
import com.atherys.towns.api.AreaObject;
import com.atherys.towns.api.BankHolder;
import com.atherys.towns.api.MetaHolder;
import com.atherys.towns.api.nation.INation;
import com.atherys.towns.api.plot.IPlot;
import com.atherys.towns.api.plot.PlotDefinition;
import com.atherys.towns.api.resident.IResident;
import com.atherys.towns.api.resident.ResidentHolder;
import com.atherys.towns.views.TownView;
import java.util.List;
import java.util.Optional;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public interface ITown extends DBObject, AreaObject, BankHolder, MetaHolder, ResidentHolder, Viewable<TownView> {

    List<IPlot> getPlots();

    Optional<INation> getNation();

    void setNation(INation nation);

    IResident getMayor();

    void setMayor(IResident resident);

    void addPlot(PlotDefinition plot);

    void removePlot(IPlot plot);

    int getMaximumSize();

    void setMaximumSize(int maxSize);

    int getSize();

    Location<World> getSpawnLocation();

    void setSpawnLocation(Location<World> location);

    default void addResident(IResident resident) {
        getResidents().add(resident);
        resident.setTown(this);
    }

    default void removeResident(IResident resident) {
        getResidents().remove(resident);
        resident.setTown(null);
    }

}
