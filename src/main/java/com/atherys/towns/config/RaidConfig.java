package com.atherys.towns.config;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@ConfigSerializable
public class RaidConfig {

    @Setting("town-raid-health")
    public double RAID_HEALTH = 500.00;

    @Setting("town-raid-duration")
    public Duration RAID_DURATION = Duration.of(1, ChronoUnit.MINUTES);

    @Setting("town-raid-cooldown")
    public Duration RAID_COOLDOWN = Duration.of(1, ChronoUnit.MINUTES);

    @Setting("town-raid-cost")
    public double RAID_COST = 500.00;

    @Setting("town-raid-spawn-radius")
    public int RAID_SPAWN_RADIUS = 450;

    @Setting("town-raid-creation-distance")
    public int RAID_MIN_CREATION_DISTANCE = 180;

    @Setting("town-raid-creation-distance")
    public int RAID_MAX_CREATION_DISTANCE = 500;

    @Setting("town-raid-damage-per-spawn")
    public int RAID_DAMAGE_PER_SPAWN = 50;

    @Setting("town-raid-distance-between-points")
    public int RAID_DISTANCE_BETWEEN_POINTS = 150;

    @Setting("town-raid-skin-uuid")
    public String RAID_SKIN_UUID = "b1759db2-3b7f-4d5d-9155-70aca6e94cba";

}
