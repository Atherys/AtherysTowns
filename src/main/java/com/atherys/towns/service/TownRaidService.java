package com.atherys.towns.service;

import com.atherys.towns.AtherysTowns;
import com.atherys.towns.TownsConfig;
import com.atherys.towns.model.RaidPoint;
import com.atherys.towns.model.entity.Town;
import com.atherys.towns.util.MathUtils;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Singleton
public class TownRaidService {
    @Inject
    TownsConfig config;

    @Inject
    TownService townService;

    Map<UUID, RaidPoint> activeRaids = new HashMap<>();

    public TownRaidService() {

    }

    public void removeRaidPoint(UUID id) {
        activeRaids.remove(id);
    }

    public RaidPoint getRaidPoint(UUID id) {
        return activeRaids.get(id);
    }

    public boolean isIdRaidEntity(UUID entityId) {
        return activeRaids.values().stream().anyMatch(point -> point.getRaidPointUUID().equals(entityId));
    }

    public Optional<RaidPoint> getTownRaidPoint(Town town) {
        return activeRaids.values().stream().filter(point -> point.getRaidingTown().equals(town)).findFirst();
    }

    public void createRaidPointEntry(Transform<World> transform, Town town, Town targetTown, UUID entityId, Set<UUID> particleEffects) {
        RaidPoint point = new RaidPoint(LocalDateTime.now(), transform, entityId, town, targetTown, particleEffects);
        townService.setTownLastRaidCreationDate(town, LocalDateTime.now());
        activeRaids.put(entityId, point);
    }

    public boolean isTownRaidActive(Town town) {
        return getTownRaidPoint(town).isPresent();
    }

    public boolean isLocationTaken(Location<World> location) {
        return activeRaids.values().stream().anyMatch(point -> MathUtils.getDistanceBetweenPoints(location.getPosition(), point.getPointTransform().getPosition()) < config.RAID.RAID_DISTANCE_BETWEEN_POINTS);
    }

    public boolean hasCooldownPeriodPassed(Town town) {
        if (town.getLastRaidCreationDate() != null) {
            return Duration.between(town.getLastRaidCreationDate(), LocalDateTime.now()).compareTo(config.RAID.RAID_COOLDOWN) >= 0;
        }
        return true;
    }

    public boolean hasDurationPassed(Town town) {
        if (town.getLastRaidCreationDate() != null) {
            return Duration.between(town.getLastRaidCreationDate(), LocalDateTime.now()).compareTo(config.RAID.RAID_DURATION) >= 0;
        }
        return true;
    }

    public void initRaidTimer() {
        Task.Builder taxTimer = Task.builder();
        taxTimer.interval(2, TimeUnit.SECONDS)
                .execute(RaidTimerTask())
                .submit(AtherysTowns.getInstance());
    }

    public void removeEntity(World world, UUID uuid) {
        Optional<Entity> entity = world.getEntity(uuid);
        entity.ifPresent(Entity::remove);
    }

    private Runnable RaidTimerTask() {
        return () -> {
            Set<UUID> pointsToRemove = new HashSet<>();
            activeRaids.forEach((uuid, point) -> {
                if (hasDurationPassed(point.getRaidingTown())) {
                    removeEntity(point.getPointTransform().getExtent(), uuid);
                    pointsToRemove.add(uuid);
                    Text raidRemovedText = Text.of("Your raid point has expired!");
                    AtherysTowns.getInstance().getTownFacade().getOnlineTownMembers(point.getRaidingTown()).forEach(player -> {
                        AtherysTowns.getInstance().getTownsMessagingService().info(player, raidRemovedText);
                    });
                }
            });
            pointsToRemove.forEach(this::removeRaidPoint);
        };
    }
}
