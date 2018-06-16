package com.atherys.towns2.base;

import com.flowpowered.math.vector.Vector3d;
import org.spongepowered.api.util.Identifiable;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.extent.Extent;

/**
 * A 2-dimensional area which may contain a location
 */
public interface LocationContainer<T extends Extent> extends Identifiable {

    /**
     * @return Whether or not it contains the given location
     */
    default boolean contains(T extent, double x, double y, double z) {
        return contains(new Location<>(extent, x, y, z));
    }

    /**
     * @return Whether or not it contains the given location
     */
    default boolean contains(T extent, Vector3d position) {
        return contains(new Location<>(extent, position));
    }

    /**
     * @return The extent of this area
     */
    T getExtent();

    /**
     * @return Whether or not it contains the given location
     */
    boolean contains(Location<T> location);

    /**
     * @return The size of this area
     */
    int getArea();

}
