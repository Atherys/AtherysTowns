package com.atherys.towns.model;

import com.atherys.core.database.api.DBObject;
import com.flowpowered.math.vector.Vector2d;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.spongepowered.api.world.World;

import java.util.UUID;

@Entity(value = "plots", noClassnameStored = true)
public class Plot implements DBObject {

    @Id
    private UUID uuid;

    private World world;

    private Vector2d min;
    private Vector2d max;

    private Town town;

    private Plot() {}

    public Plot(Town town, Vector2d min, Vector2d max) {
        this.uuid = UUID.randomUUID();
        this.town = town;
        this.world = town.getWorld();
        this.min = min;
        this.max = max;
    }

    @Override
    public UUID getUniqueId() {
        return uuid;
    }

    public World getWorld() {
        return world;
    }

    public Vector2d getMin() {
        return min;
    }

    public Vector2d getMax() {
        return max;
    }

    public Town getTown() {
        return town;
    }

}
