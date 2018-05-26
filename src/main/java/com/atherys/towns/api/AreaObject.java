package com.atherys.towns.api;

import math.geom2d.Point2D;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

/**
 * An AreaObject is defined as any arbitrarily shaped 2-dimensional area which can contain points.
 */
public interface AreaObject {

    boolean contains(World w, double x, double y);

    default boolean contains(World w, Point2D point) {
        return contains(w, point.x(), point.y());
    }

    default boolean contains(Location<World> loc) {
        return contains(loc.getExtent(), loc.getPosition().getX(), loc.getPosition().getY() );
    }

}
