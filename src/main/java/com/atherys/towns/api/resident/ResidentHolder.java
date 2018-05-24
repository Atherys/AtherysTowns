package com.atherys.towns.api.resident;

import java.util.Collection;

public interface ResidentHolder {

    <T extends IResident> Collection<T> getResidents();

    default <T extends IResident> boolean containsResident(T resident) {
        return getResidents().contains(resident);
    }

}
