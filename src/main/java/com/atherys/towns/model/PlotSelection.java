package com.atherys.towns.model;

import com.flowpowered.math.vector.Vector2d;
import com.flowpowered.math.vector.Vector2i;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class PlotSelection {

    private Location<World> pointA;

    private Location<World> pointB;

    public PlotSelection() {
    }

    public Location<World> getPointA() {
        return pointA;
    }

    public Vector2d getPointAVector() {
        return pointA.getPosition().toVector2(true);
    }

    public void setPointA(Location<World> pointA) {
        this.pointA = pointA;
    }

    public Location<World> getPointB() {
        return pointB;
    }

    public Vector2d getPointBVector() {
        return pointB.getPosition().toVector2(true);
    }

    public void setPointB(Location<World> pointB) {
        this.pointB = pointB;
    }

    public boolean isComplete() {
        return pointA != null && pointB != null;
    }
}
