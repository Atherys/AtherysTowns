package com.atherys.towns.model;

import com.atherys.core.database.api.DBObject;
import com.atherys.towns.model.permission.ResidentRank;
import com.atherys.towns.model.permission.RankDefinitionStorage;
import com.flowpowered.math.vector.Vector2d;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.spongepowered.api.world.World;

import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

@Entity(value = "plots", noClassnameStored = true)
public class Plot implements DBObject, RankDefinitionStorage {

    @Id
    private UUID uuid;

    private World world;

    private Vector2d min;
    private Vector2d max;

    private Town town;

    private Set<ResidentRank> residentRanks = new TreeSet<>();

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

    @Override
    public Type getPermissionContext() {
        return Type.PLOT;
    }

    @Override
    public Set<ResidentRank> getDefinedResidentRanks() {
        return residentRanks;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Plot plot = (Plot) o;
        return Objects.equals(uuid, plot.uuid) &&
                Objects.equals(world.getUniqueId(), plot.world.getUniqueId()) &&
                Objects.equals(min, plot.min) &&
                Objects.equals(max, plot.max) &&
                Objects.equals(town, plot.town);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid, world, min, max, town);
    }
}
