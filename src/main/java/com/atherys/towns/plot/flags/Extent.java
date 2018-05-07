package com.atherys.towns.plot.flags;

import com.atherys.towns.plot.Plot;
import com.atherys.towns.resident.Resident;
import org.spongepowered.api.CatalogType;
import org.spongepowered.api.util.annotation.CatalogedBy;

@CatalogedBy(Extents.class)
public class Extent implements CatalogType {

    @FunctionalInterface
    protected interface Checker {

        boolean apply(Resident resident, Flag flag, Plot plot);
    }

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
}
