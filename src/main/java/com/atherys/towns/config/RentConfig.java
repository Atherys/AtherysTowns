package com.atherys.towns.config;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.time.Duration;

@ConfigSerializable
public class RentConfig {
    @Setting("plots-rentable")
    public int PLOTS_RENTABLE = 10;

    @Setting("minimum-rent-cost")
    public double MINIMUM_RENT_COST = 0.0;

    @Setting("maximum-rent-cost")
    public double MAXIMUM_RENT_COST = 100000;

    @Setting("minimum-rent-period")
    public Duration MINIMUM_RENT_PERIOD = Duration.ofDays(7);

    @Setting("maximum-rent-period")
    public Duration MAXIMUM_RENT_PERIOD = Duration.ofDays(31);

    @Setting("rent-check-interval")
    public Duration RENT_CHECK_INTERVAL = Duration.ofHours(12);
}
