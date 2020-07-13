package com.atherys.towns.config;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.time.Duration;

@ConfigSerializable
public class RaidConfig {

    @Setting("town-raid-health")
    public int RAID_HEALTH = 1024;

    @Setting("town-raid-duration")
    public Duration RAID_DURATION;

    @Setting("town-raid-cooldown")
    public Duration RAID_COOLDOWN;

    @Setting("town-raid-cost")
    public double RAID_COST;

    @Setting("town-raid-spawn-radius")
    public int RAID_SPAWN_RADIUS;

    @Setting("town-raid-creation-distance")
    public int RAID_CREATION_DISTANCE;

}
