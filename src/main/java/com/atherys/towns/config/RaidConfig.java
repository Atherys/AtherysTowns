package com.atherys.towns.config;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@ConfigSerializable
public class RaidConfig {

    @Setting("town-raid-health")
    public double RAID_HEALTH = 1024.00;

    @Setting("town-raid-duration")
    public Duration RAID_DURATION = Duration.of(1, ChronoUnit.MINUTES);

    @Setting("town-raid-cooldown")
    public Duration RAID_COOLDOWN = Duration.of(1, ChronoUnit.MINUTES);

    @Setting("town-raid-cost")
    public double RAID_COST;

    @Setting("town-raid-spawn-radius")
    public int RAID_SPAWN_RADIUS;

    @Setting("town-raid-creation-distance")
    public int RAID_CREATION_DISTANCE;

    @Setting("town-raid-damage-per-spawn")
    public int RAID_DAMAGE_PER_SPAWN = 50;

}
