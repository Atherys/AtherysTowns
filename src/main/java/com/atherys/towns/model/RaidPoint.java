package com.atherys.towns.model;

import com.atherys.towns.model.entity.Town;
import org.spongepowered.api.boss.ServerBossBar;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.world.World;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public class RaidPoint {

    private Transform<World> pointTransform;

    private LocalDateTime creationTime;

    private UUID raidPointUUID;

    private Set<UUID> particleUUIDs;

    private Town raidingTown;

    private Town targetTown;

    private ServerBossBar raidBossBar;

    public RaidPoint(LocalDateTime creationTime, Transform<World> location, UUID entityId, Town raidingTown,
                     Town targetTown, Set<UUID> particleUUIDs, ServerBossBar raidBossBar) {
        this.pointTransform = location;
        this.creationTime = creationTime;
        this.raidPointUUID = entityId;
        this.raidingTown = raidingTown;
        this.targetTown = targetTown;
        this.particleUUIDs = particleUUIDs;
        this.raidBossBar = raidBossBar;
    }

    public Transform<World> getPointTransform() {
        return this.pointTransform;
    }

    public void setPointTransform(Transform<World> pointTransform) {
        this.pointTransform = pointTransform;
    }

    public LocalDateTime getCreationTime() {
        return this.creationTime;
    }

    public void setCreationTime(LocalDateTime creationTime) {
        this.creationTime = creationTime;
    }

    public UUID getRaidPointUUID() {
        return this.raidPointUUID;
    }

    public void setRaidPointUUID(UUID raidPointUUID) {
        this.raidPointUUID = raidPointUUID;
    }

    public Town getRaidingTown() {
        return this.raidingTown;
    }

    public void setRaidingTown(Town town) {
        this.raidingTown = town;
    }

    public Town getTargetTown() {
        return this.targetTown;
    }

    public void setTargetTown(Town targetTown) {
        this.targetTown = targetTown;
    }

    public Set<UUID> getParticleUUIDs() {
        return this.particleUUIDs;
    }

    public void setParticleUUIDs(Set<UUID> particleUUIDs) {
        this.particleUUIDs = particleUUIDs;
    }

    public ServerBossBar getRaidBossBar() {
        return this.raidBossBar;
    }

    public void setRaidBossBar(ServerBossBar raidBossBar) {
        this.raidBossBar = raidBossBar;
    }
}
