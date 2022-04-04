package com.atherys.towns.config;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.time.Duration;

@ConfigSerializable
public class TaxConfig {

    @Setting("is-enabled")
    public boolean IS_ENABLED = false;

    @Setting("tax-collection-interval")
    public Duration TAX_COLLECTION_DURATION = Duration.ofHours(12);

    @Setting("tax-timer-interval")
    public Duration TAX_TIMER_INTERVAL = Duration.ofMinutes(1);

    @Setting("inactive-resident-duration")
    public Duration INACTIVE_DURATION = Duration.ofDays(14);

    @Setting("base-tax")
    public int BASE_TAX = 100;

    @Setting("per-resident-tax")
    public int PER_RESIDENT_TAX = 5;

    @Setting("per-block-area-tax")
    public double PER_BLOCK_AREA_TAX = 0.1;

    @Setting("oversize-area-tax-modifier")
    public double OVERSIZE_AREA_TAX_MODIFIER = 0.5;
    
    @Setting("pvp-tax-modifier")
    public double PVP_TAX_MODIFIER = 1.25;

    @Setting("min-nation-modifier")
    public double MIN_NATION_TAX_MODIFIER = 1.0;

    @Setting("max-nation-modifier")
    public double MAX_NATION_TAX_MODIFIER = 2.0;

    @Setting("void-rate")
    public double VOID_RATE = 0.2;

    @Setting("max-tax-failures-before-ruin")
    public int MAX_TAX_FAILURES = 3;
}
