package com.atherys.towns.api;

public final class WorldPermissions {

    public static final Permission BUILD = new Permission("world_build", "Build");

    public static final Permission DESTROY = new Permission("world_destroy", "Destroy");

    public static final Permission DAMAGE_NONPLAYERS = new Permission("world_damage_nonplayers", "Damage Non-Players");

    public static final Permission DAMAGE_PLAYERS = new Permission("world_damage_players", "Damage Players");

    public static final Permission INTERACT_DOORS = new Permission("world_interact_doors", "Interact with Doors");

    public static final Permission INTERACT_REDSTONE = new Permission("world_interact_redstone", "Interact with Redstone");

    public static final Permission INTERACT_CHESTS = new Permission("world_interact_chests", "Interact with Chests");

}
