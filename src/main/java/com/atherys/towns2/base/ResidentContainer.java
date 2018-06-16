package com.atherys.towns2.base;

import com.atherys.towns2.resident.Resident;
import java.util.Set;
import org.spongepowered.api.service.context.ContextSource;

public interface ResidentContainer extends ContextSource {

    Set<Resident> getResidents();

    default boolean addResident(Resident resident) {
        return getResidents().add(resident);
    }

    default boolean removeResident(Resident resident) {
        return getResidents().remove(resident);
    }

}
