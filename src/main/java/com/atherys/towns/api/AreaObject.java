package com.atherys.towns.api;

import com.atherys.core.database.api.DBObject;
import math.geom2d.Point2D;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

/**
 * An AreaObject is defined as any arbitrarily shaped 2-dimensional area which can contain points.
 * AreaObjects can have parents, but this is not guaranteed.<br> The tree for AreaObjects in the
 * Towns plugin is as follows:<br> <br> null<br> | is a parent of<br> Nation<br> | is a parent
 * of<br> Town<br> | is a parent of<br> Plot<br>
 */
public interface AreaObject extends DBObject {

    boolean contains(World w, double x, double y);

    boolean contains(World w, Point2D point);

    boolean contains(Location<World> loc);

}
