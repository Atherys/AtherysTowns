package com.atherys.towns.api.nation;

import com.atherys.core.database.api.DBObject;
import com.atherys.core.views.Viewable;
import com.atherys.towns.api.AreaObject;
import com.atherys.towns.api.BankHolder;
import com.atherys.towns.api.MetaHolder;
import com.atherys.towns.api.resident.IResident;
import com.atherys.towns.api.resident.ResidentHolder;
import com.atherys.towns.api.town.ITown;
import com.atherys.towns.views.NationView;
import java.util.List;
import java.util.Optional;

public interface INation extends DBObject, AreaObject, MetaHolder, BankHolder, ResidentHolder, Viewable<NationView> {

    List<ITown> getTowns();

    Optional<ITown> getCapital();

    void setCapital(ITown town);

    Optional<IResident> getLeader();

    void setLeader(IResident resident);

    double getTax();

    void setTax(double tax);

    default void addTown(ITown town) {
        getTowns().add(town);
        town.setNation(this);
    }

    default void removeTown(ITown town) {
        getTowns().remove(town);
        town.setNation(null);
    }

}
