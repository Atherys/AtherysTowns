package com.atherys.towns.api.plot.flag;

import com.atherys.towns.api.resident.IResident;
import com.atherys.towns.resident.Resident;
import org.spongepowered.api.util.Tristate;

import java.util.Map;
import java.util.Optional;

public interface FlagHolder {

    Map<Flag,Extent> getFlags();

    default void setExtent(Flag flag, Extent extent) {
        getFlags().put(flag, extent);
    }

    default Optional<Extent> getExtent(Flag flag) {
        return Optional.ofNullable(getFlags().get(flag));
    }

    default boolean isPermitted(IResident resident, Flag flag) {
        return getExtent(flag).map(extent -> extent.check(resident, this)).orElse(false);
    }

    default void permit(Resident resident, Flag flag) {
        resident.modify(flag, this, Tristate.TRUE);
    }

    default void restrict(Resident resident, Flag flag) {
        resident.modify(flag, this, Tristate.FALSE);
    }

}
