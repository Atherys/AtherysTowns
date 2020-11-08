package com.atherys.towns.config;

import com.atherys.towns.api.permission.TownsPermissionContext;
import com.atherys.towns.api.permission.world.WorldPermission;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class PlotPermissionConfig {

    @Setting("context")
    private TownsPermissionContext context;

    @Setting("world-permission")
    private WorldPermission worldPermission;

    public PlotPermissionConfig() {
    }

    public PlotPermissionConfig(TownsPermissionContext context, WorldPermission worldPermission) {
        this.context = context;
        this.worldPermission = worldPermission;
    }

    public TownsPermissionContext getContext() {
        return context;
    }

    public void setContext(TownsPermissionContext context) {
        this.context = context;
    }

    public WorldPermission getWorldPermission() {
        return worldPermission;
    }

    public void setWorldPermission(WorldPermission worldPermission) {
        this.worldPermission = worldPermission;
    }
}
