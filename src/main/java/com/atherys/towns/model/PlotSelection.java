package com.atherys.towns.model;

import com.flowpowered.math.vector.Vector3d;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class PlotSelection {

    private Location<World> pointA;

    private Location<World> pointB;

    private boolean isCuboid;

    public PlotSelection() {
    }

    public Location<World> getPointA() {
        return pointA;
    }

    public Vector3d getPointAVector() {
        return pointA.getPosition();
    }

    public void setPointA(Location<World> pointA) {
        this.pointA = pointA;
    }

    public Location<World> getPointB() {
        return pointB;
    }

    public Vector3d getPointBVector() {
        return pointB.getPosition();
    }

    public void setPointB(Location<World> pointB) {
        this.pointB = pointB;
    }

    public boolean isComplete() {
        return pointA != null && pointB != null;
    }

    public boolean isCuboid() {
        return isCuboid;
    }

    public void setCuboid(boolean cuboid) {
        isCuboid = cuboid;
    }
}
