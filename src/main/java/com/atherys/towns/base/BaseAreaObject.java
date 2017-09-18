package com.atherys.towns.base;

import math.geom2d.Point2D;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;

public interface BaseAreaObject extends TownsObject {

    Optional<? extends BaseAreaObject> getParent();

    boolean contains ( World w, double x, double y );

    boolean contains ( World w, Point2D point );

    boolean contains ( Location<World> loc );

}
