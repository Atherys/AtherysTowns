package com.atherys.towns.config;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class TownSizeAutomationConfig {

    @Setting("enabled")
    public boolean IS_ENABLED = false;

    @Setting("area-granted-per-active-resident")
    public int AREA_GRANTED_PER_ACTIVE_RESIDENT = 256;

}
