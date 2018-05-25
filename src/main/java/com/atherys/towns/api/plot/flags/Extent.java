package com.atherys.towns.api.plot.flags;

import com.atherys.towns.api.plot.IPlot;
import com.atherys.towns.api.plot.flag.FlagHolder;
import com.atherys.towns.api.plot.flag.IExtent;
import com.atherys.towns.api.resident.IResident;
import com.atherys.towns.plot.Plot;
import com.atherys.towns.resident.Resident;
import org.spongepowered.api.util.annotation.CatalogedBy;

@CatalogedBy(Extents.class)
public class Extent implements IExtent {

    private String id;
    private String name;
    private Checker checker;

    protected Extent(String id, String name, Checker checker) {
        this.id = id;
        this.name = name;
        this.checker = checker;

        ExtentRegistry.getInstance().extents.put(id, this);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    public boolean check(Resident res, Flag flag, Plot plot) {
        return checker.apply(res, flag, plot);
    }

    @Override
    public String toString() {
        return getId();
    }

    @Override
    public <R extends IResident, P extends IPlot> boolean check(R resident, FlagHolder holder) {
        return false;
    }

    @FunctionalInterface
    protected interface Checker {

        boolean apply(Resident resident, FlagHolder plot);
    }
}
