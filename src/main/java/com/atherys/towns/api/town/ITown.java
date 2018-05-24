package com.atherys.towns.api.town;

import com.atherys.towns.api.AreaObject;
import com.atherys.towns.api.BankHolder;
import com.atherys.towns.api.MetaHolder;
import com.atherys.towns.api.plot.flag.FlagHolder;
import com.atherys.towns.api.resident.IResident;
import com.atherys.towns.api.resident.ResidentHolder;
import com.atherys.towns.nation.Nation;

import java.util.Optional;

public interface ITown extends AreaObject, BankHolder, MetaHolder, FlagHolder, ResidentHolder {

    Optional<Nation> getNation();

    <T extends IResident> T getMayor();

    <T extends IResident> void setMayor(T resident);

    default <T extends IResident> void addResident(T resident) {
        getResidents().add(resident);
        resident.setTown(this);
    }

    default <T extends IResident> void removeResident(T resident) {
        getResidents().remove(resident);
        resident.setTown(null);
    }

}
