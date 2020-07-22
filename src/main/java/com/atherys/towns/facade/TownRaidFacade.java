package com.atherys.towns.facade;

import com.atherys.core.economy.Economy;
import com.atherys.towns.AtherysTowns;
import com.atherys.towns.TownsConfig;
import com.atherys.towns.api.command.TownsCommandException;
import com.atherys.towns.model.RaidPoint;
import com.atherys.towns.model.entity.Plot;
import com.atherys.towns.model.entity.Resident;
import com.atherys.towns.model.entity.Town;
import com.atherys.towns.service.PlotService;
import com.atherys.towns.service.ResidentService;
import com.atherys.towns.service.TownRaidService;
import com.atherys.towns.util.MathUtils;
import com.flowpowered.math.vector.Vector3d;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.effect.particle.ParticleType;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.entity.AreaEffectCloud;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.entity.DestructEntityEvent;
import org.spongepowered.api.event.entity.living.humanoid.player.RespawnPlayerEvent;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

import static org.spongepowered.api.text.format.TextColors.*;

@Singleton
public class TownRaidFacade {

    @Inject
    TownsConfig config;

    @Inject
    TownFacade townFacade;

    @Inject
    TownsMessagingFacade townsMsg;

    @Inject
    TownRaidService townRaidService;

    @Inject
    ResidentService residentService;

    @Inject
    PlotService plotService;

    public TownRaidFacade() {

    }

    public boolean isEntityRaidPoint(Entity entity) {
        return townRaidService.isIdRaidEntity(entity.getUniqueId());
    }

    public void checkDistanceToTown(Town town, Vector3d targetPoint) throws TownsCommandException {
        for (Plot plot : town.getPlots()) {
            double plotDistance = MathUtils.getDistanceToPlot(MathUtils.vec3dToVec2i(targetPoint), plot.getNorthEastCorner(), plot.getSouthWestCorner());
            if (plotDistance < config.RAID.RAID_MIN_CREATION_DISTANCE) {
                throw new TownsCommandException("Target town is too close to current location!");
            }
            if (plotDistance < config.RAID.RAID_MAX_CREATION_DISTANCE) {
                return;
            }
            throw new TownsCommandException("Target town is too far away from current location!");
        }
    }

    public boolean isPlayerCloseToRaid(Transform<World> targetSpawn, Transform<World> spawnLocation) {
        return MathUtils.getDistanceBetweenPoints(spawnLocation.getPosition(), targetSpawn.getPosition()) <= config.RAID.RAID_SPAWN_RADIUS;
    }

    public void validateRaid(Town town, Transform<World> transform, Town targetTown) throws TownsCommandException {
        Location<World> location = transform.getLocation();

        if (AtherysTowns.economyIsEnabled()) {
            Optional<Account> townBank = Economy.getAccount(town.getBank().toString());
            if (townBank.isPresent() && townBank.get().getBalance(config.DEFAULT_CURRENCY).doubleValue() < config.RAID.RAID_COST) {
                throw new TownsCommandException("Your town bank does not have enough money to create a raid point!");
            }
        }

        if ((town.getNation() != null && targetTown.getNation() != null)
                && !town.getNation().getEnemies().contains(targetTown.getNation())) {
            throw new TownsCommandException("Target town is not an enemy of your nation!");
        }

        if (townRaidService.isTownRaidActive(town)) {
            throw new TownsCommandException("Your town already has an active raid point!");
        }

        if (townRaidService.isLocationTaken(location)) {
            throw new TownsCommandException("This location is too close to another town's raid point!");
        }

        if (!townRaidService.hasCooldownPeriodPassed(town)) {
            throw new TownsCommandException("Raid Cooldown is still in effect!");
        }

        if (plotService.getPlotByLocation(location).isPresent()) {
            throw new TownsCommandException("You cannot create a raid point within the borders of a town!");
        }

        checkDistanceToTown(targetTown, transform.getPosition());
    }

    public AreaEffectCloud getEffectCloud(ParticleType type, double verticalAdjustment, Transform<World> transform) {
        AreaEffectCloud cloud = (AreaEffectCloud) transform.getExtent().createEntity(EntityTypes.AREA_EFFECT_CLOUD, transform.getPosition().add(0, verticalAdjustment, 0));
        cloud.offer(Keys.AREA_EFFECT_CLOUD_RADIUS, 2.0);
        cloud.offer(Keys.AREA_EFFECT_CLOUD_PARTICLE_TYPE, type);
        cloud.offer(Keys.AREA_EFFECT_CLOUD_DURATION, (int) config.RAID.RAID_DURATION.getSeconds() * 20);

        return cloud;
    }

    public Set<UUID> spawnPortalParticles(Transform<World> transform, Player player) {
        AreaEffectCloud cloud1 = getEffectCloud(ParticleTypes.CLOUD, -0.1, transform);
        AreaEffectCloud cloud2 = getEffectCloud(ParticleTypes.ENCHANTING_GLYPHS, 0.5, transform);
        player.getWorld().spawnEntity(cloud1);
        player.getWorld().spawnEntity(cloud2);

        return new HashSet<>(Arrays.asList(cloud1.getUniqueId(), cloud2.getUniqueId()));
    }

    public UUID spawnRaidPoint(Transform<World> transform, Player player) {
        Entity entity = player.getWorld().createEntity(EntityTypes.HUMAN, transform.getPosition().add(0, 1, -2));

        entity.offer(Keys.AI_ENABLED, false);
        entity.offer(Keys.DISPLAY_NAME, Text.of("Hired Mage"));
        entity.offer(Keys.MAX_HEALTH, config.RAID.RAID_HEALTH);
        entity.offer(Keys.HEALTH, config.RAID.RAID_HEALTH);
        entity.offer(Keys.SKIN_UNIQUE_ID, UUID.fromString(config.RAID.RAID_SKIN_UUID));

        player.getWorld().spawnEntity(entity);

        return entity.getUniqueId();
    }

    private Text formatRaidLocation(Transform<World> transform) {
        Location<World> location = transform.getLocation();
        return Text.of(GOLD, "x: ", location.getBlockX(), ", y: ", location.getBlockY(), ", z: ", location.getBlockZ());
    }

    private Text formatDurationLeft(LocalDateTime time, Duration duration) {
        Duration durationBetweenPresent = Duration.between(time, LocalDateTime.now());
        Duration durationLeft = duration.minus(durationBetweenPresent);
        if (durationLeft.toMinutes() >= 1) {
            return Text.of(GOLD, durationLeft.toMinutes(), " minute(s)");
        }
        return Text.of(GOLD, durationLeft.getSeconds(), " second(s)");
    }

    public void sendRaidPointInfo(Player player) throws TownsCommandException {
        Town town = townFacade.getPlayerTown(player);
        boolean raidExists = townRaidService.isTownRaidActive(town);
        Optional<RaidPoint> point = townRaidService.getTownRaidPoint(town);
        Text status = raidExists ? Text.of(GREEN, "True") : Text.of(RED, "False");
        Text pointLocation = raidExists ? formatRaidLocation(point.get().getPointTransform()) : Text.of(RED, "No Raid in Progress");
        Text pointHealth = raidExists ? Text.of(GOLD, townRaidService.getRaidHealth(player.getWorld(), point.get())) : Text.of(RED, "No Raid in Progress");
        Text durationLeft = raidExists ? formatDurationLeft(point.get().getCreationTime(), config.RAID.RAID_DURATION) : Text.of(RED, "No Raid in Progress");
        Text cooldown = townRaidService.hasCooldownPeriodPassed(town) ? Text.of(GREEN, "No Cooldown Remaining") : formatDurationLeft(town.getLastRaidCreationDate(), config.RAID.RAID_COOLDOWN);

        Text.Builder raidText = Text.builder();

        raidText
                .append(townsMsg.createTownsHeader("Raid Point"));

        raidText
                .append(Text.of(DARK_GREEN, "Raid in Progress: ", status, Text.NEW_LINE))
                .append(Text.of(DARK_GREEN, "Raid Location: ", pointLocation, Text.NEW_LINE))
                .append(Text.of(DARK_GREEN, "Point Health: ", pointHealth, Text.NEW_LINE))
                .append(Text.of(DARK_GREEN, "Duration Left: ", durationLeft, Text.NEW_LINE))
                .append(Text.of(DARK_GREEN, "Cooldown: ", cooldown));

        player.sendMessage(raidText.build());
    }

    public Transform<World> getRaidPointTransform(Player player) {
        Vector3d playerVector = player.getTransform().getPosition();
        Location<World> highLocation = player.getLocation().asHighestLocation();
        Location<World> newLocation = new Location<>(player.getWorld(), playerVector.getX(), highLocation.getBlockY(), playerVector.getZ());
        return player.getTransform().setLocation(newLocation);
    }

    public void createRaidPoint(Player player, Town targetTown) throws TownsCommandException {
        Town town = townFacade.getPlayerTown(player);
        Transform<World> transform = getRaidPointTransform(player);
        validateRaid(town, transform, targetTown);

        UUID entityId = spawnRaidPoint(transform, player);
        Set<UUID> particleEffects = spawnPortalParticles(transform, player);
        if (AtherysTowns.economyIsEnabled()) {
            Economy.getAccount(town.getBank().toString()).ifPresent(account -> {
                Cause cause = Sponge.getCauseStackManager().getCurrentCause();
                account.withdraw(config.DEFAULT_CURRENCY, BigDecimal.valueOf(config.RAID.RAID_COST), cause);
                townsMsg.info(player, Text.of("You've paid ", config.RAID.RAID_COST, " from the town bank to hire a mage!"));
            });
        }
        townRaidService.createRaidPointEntry(transform, town, targetTown, entityId, particleEffects);

        townFacade.getOnlineTownMembers(town).forEach(member -> townsMsg.info(member, "A mage has been deployed to assist you with your raid!"));
    }

    public void removeRaidPoint(Player player) throws TownsCommandException {
        Resident resident = residentService.getOrCreate(player);
        if (townRaidService.isTownRaidActive(resident.getTown())) {
            Optional<RaidPoint> raidPoint = townRaidService.getTownRaidPoint(resident.getTown());
            raidPoint.ifPresent(point -> {
                townRaidService.removeEntity(player.getWorld(), point.getRaidPointUUID());
                point.getParticleUUIDs().forEach(uuid -> townRaidService.removeEntity(point.getPointTransform().getExtent(), uuid));
                townRaidService.removeRaidPoint(point.getRaidPointUUID());
            });
            townFacade.getOnlineTownMembers(resident.getTown()).forEach(member -> townsMsg.info(member, "Your mage has been relieved of duty!"));
        } else {
            throw new TownsCommandException("You do not have a town raid currently active!");
        }

    }

    public void onRaidPointDeath(DestructEntityEvent.Death event) {
        RaidPoint point = townRaidService.getRaidPoint(event.getTargetEntity().getUniqueId());
        townFacade.getOnlineTownMembers(point.getRaidingTown()).forEach(member -> townsMsg.info(member, "Your mage has been killed!"));
        point.getParticleUUIDs().forEach(uuid -> townRaidService.removeEntity(point.getPointTransform().getExtent(), uuid));
        townRaidService.removeRaidPoint(point.getRaidPointUUID());
    }

    public boolean onPlayerSpawn(RespawnPlayerEvent event) {
        Town town = residentService.getOrCreate(event.getOriginalPlayer()).getTown();
        if (townRaidService.isTownRaidActive(town)) {
            Optional<RaidPoint> point = townRaidService.getTownRaidPoint(town);
            if (point.isPresent() && isPlayerCloseToRaid(event.getFromTransform(), point.get().getPointTransform())) {
                event.setToTransform(point.get().getPointTransform());
                event.getOriginalPlayer().getWorld().getEntity(point.get().getRaidPointUUID()).ifPresent(entity -> {
                    double initialHealth = entity.get(Keys.HEALTH).orElse(0.0);
                    entity.offer(Keys.HEALTH, initialHealth - config.RAID.RAID_DAMAGE_PER_SPAWN);
                });
                return true;
            }
        }
        return false;
    }


}
