package com.atherys.towns.api.resident;

import java.util.Collection;

public interface ResidentHolder {

    Collection<IResident> getResidents();

    default boolean containsResident(IResident resident) {
        return getResidents().contains(resident);
    }

}
