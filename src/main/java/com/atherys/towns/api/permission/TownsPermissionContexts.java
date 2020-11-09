package com.atherys.towns.api.permission;

public final class TownsPermissionContexts {

    public static final TownsPermissionContext ALL = new TownsPermissionContext("atherys:all", "All");

    public static final TownsPermissionContext ALLY = new TownsPermissionContext("atherys:ally", "Ally");

    public static final TownsPermissionContext NEUTRAL = new TownsPermissionContext("atherys:neutral", "Neutral");

    public static final TownsPermissionContext ENEMY = new TownsPermissionContext("atherys:enemy", "Enemy");

    public static final TownsPermissionContext FRIEND = new TownsPermissionContext("atherys:friend", "Friend");

    public static final TownsPermissionContext TOWN = new TownsPermissionContext("atherys:town", "Town");

}
