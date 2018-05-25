package com.atherys.towns.api.plot.flag;

import com.atherys.towns.api.plot.IPlot;
import com.atherys.towns.api.resident.IResident;
import org.spongepowered.api.CatalogType;

/**
 * An extent represents a vague collection of players which may perform a given action based on a Flag
 */
public interface IExtent extends CatalogType {

    /**
     * Checks if the given IResident fits within this extent in the context of the FlagHolder
     *
     * @param resident The resident to check
     * @param holder   The FlagHolder whose context is to be checked
     * @return Whether or not the IResident fits within this extent
     */
    <R extends IResident, P extends IPlot> boolean check(R resident, FlagHolder holder);

}
