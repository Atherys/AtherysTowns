package com.atherys.towns.config;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.time.Duration;

@ConfigSerializable
public class TaxConfig {

    @Setting("tax-collection-interval")
    public Duration TAX_COLLECTION_DURATION = Duration.ofMinutes(1);

    @Setting("tax-timer-interval-minutes")
    public int TAX_COLLECTION_TIMER_MINUTES = 1;

    @Setting("base-tax-amount")
    public int BASE_TAX = 100;

    @Setting("resident-tax-amount")
    public int RESIDENT_TAX = 5;

    @Setting("area-tax-amount")
    public int AREA_TAX = 10;

    @Setting("area-oversize-tax-amount")
    public int AREA_OVERSIZE_TAX = 10;

    @Setting("min-nation-multiplier")
    public double MIN_NATION_TAX_MULTIPLIER = 0.1;

    @Setting("max-nation-multiplier")
    public double MAX_NATION_TAX_MULTIPLIER = 1.0;

    @Setting("max-tax-failures-before-ruin")
    public int MAX_TAX_FAILURES = 1;

}
