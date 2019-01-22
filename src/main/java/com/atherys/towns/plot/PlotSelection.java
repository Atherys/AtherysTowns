package com.atherys.towns.plot;

import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class PlotSelection {

    private Location<World> pointA;

    private Location<World> pointB;

    public PlotSelection() {
    }

    public void setPointA(Location<World> pointA) {
        this.pointA = pointA;
    }

    public void setPointB(Location<World> pointB) {
        this.pointB = pointB;
    }

    public Location<World> getPointA() {
        return pointA;
    }

    public Location<World> getPointB() {
        return pointB;
    }

    public boolean isComplete() {
        return pointA != null && pointB != null;
    }
}
