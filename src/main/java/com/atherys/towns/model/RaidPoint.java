package com.atherys.towns.model;

import com.atherys.towns.model.entity.Town;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.time.LocalDateTime;
import java.util.UUID;

public class RaidPoint {

    private Location<World> pointLocation;

    private LocalDateTime creationTime;

    private int health;

    public RaidPoint(LocalDateTime creationTime, Location<World> location, int health) {
        this.pointLocation = location;
        this.creationTime = creationTime;
        this.health = health;
    }

    public Location<World> getPointLocation() {
        return this.pointLocation;
    }

    public void setPointLocation(Location<World> pointLocation) {
        this.pointLocation = pointLocation;
    }

    public LocalDateTime getCreationTime() {
        return this.creationTime;
    }

    public void setCreationTime(LocalDateTime creationTime) {
        this.creationTime = creationTime;
    }

    public int getHealth() {
        return this.health;
    }

    public void setHealth(int health) {
        this.health = health;
    }
}
