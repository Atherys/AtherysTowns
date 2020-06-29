package com.atherys.towns.api.permission.world;

public final class WorldPermissions {

    // permission for the player to place blocks in plots belonging to the town the player is currently standing in
    public static final WorldPermission BUILD = new WorldPermission("atherystowns.world.build", "Build");

    // permission for the player to destory blocks in plots belonging to the town the player is currently standing in
    public static final WorldPermission DESTROY = new WorldPermission("atherystowns.world.destroy", "Destroy");

    // permission to damage non-player entities within plots belonging to the town the player is currently standing in
    public static final WorldPermission DAMAGE_NONPLAYERS = new WorldPermission("atherystowns.world.damage.nonplayers", "Damage Non-Players");

    // permission to damage other players within plots belonging to the town the player is currently standing in
    public static final WorldPermission DAMAGE_PLAYERS = new WorldPermission("atherystowns.world.damage.players", "Damage Players");

    // permission to interact with doors within plots belonging to the town the player is currently standing in
    public static final WorldPermission INTERACT_DOORS = new WorldPermission("atherystowns.world.interact.doors", "Interact with Doors");

    // permission to interact with redstone within plots belonging to the town the player is currently standing in
    public static final WorldPermission INTERACT_REDSTONE = new WorldPermission("atherystowns.world.interact.redstone", "Interact with Redstone");

    // permission to interact with chests within plots belonging to the town the player is currently standing in
    public static final WorldPermission INTERACT_CHESTS = new WorldPermission("atherystowns.world.interact.chests", "Interact with Chests");

    // permission to interact with entities within plots belonging to the town the player is currently standing in
    public static final WorldPermission INTERACT_ENTITIES = new WorldPermission("atherystowns.world.interact.entities", "Interact with Entities");

}
