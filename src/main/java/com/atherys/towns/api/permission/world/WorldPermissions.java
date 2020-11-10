package com.atherys.towns.api.permission.world;

public final class WorldPermissions {

    // permission for the player to place blocks in plots belonging to the town the player is currently standing in
    public static final WorldPermission BUILD = new WorldPermission("atherystowns.world.build", "build", "Build");

    // permission for the player to destory blocks in plots belonging to the town the player is currently standing in
    public static final WorldPermission DESTROY = new WorldPermission("atherystowns.world.destroy", "destroy", "Destroy");

    // permission to damage non-player entities within plots belonging to the town the player is currently standing in
    public static final WorldPermission DAMAGE_NONPLAYERS = new WorldPermission("atherystowns.world.damage.nonplayers", "damage_nonplayers", "Damage Non-Players");

    // permission to damage other players within plots belonging to the town the player is currently standing in
    public static final WorldPermission DAMAGE_PLAYERS = new WorldPermission("atherystowns.world.damage.players", "damage_players","Damage Players");

    // permission to interact with doors within plots belonging to the town the player is currently standing in
    public static final WorldPermission INTERACT_DOORS = new WorldPermission("atherystowns.world.interact.doors", "interact_doors", "Interact with Doors");

    // permission to interact with redstone within plots belonging to the town the player is currently standing in
    public static final WorldPermission INTERACT_REDSTONE = new WorldPermission("atherystowns.world.interact.redstone", "interact_redstone", "Interact with Redstone");

    // permission to interact with any tile entity within plots belonging to the town the player is currently standing in
    public static final WorldPermission INTERACT_TILE_ENTITIES = new WorldPermission("atherystowns.world.interact.tile", "interact_tile_entities", "Interact with Tile Entities");

    // permission to interact with entities within plots belonging to the town the player is currently standing in
    public static final WorldPermission INTERACT_ENTITIES = new WorldPermission("atherystowns.world.interact.entities", "interact_entities", "Interact with Entities");

    // permission to interact with entities within plots belonging to the town the player is currently standing in
    public static final WorldPermission SPAWN_ENTITIES = new WorldPermission("atherystowns.world.spawn.entities", "spawn_entities", "Spawn Entities ( WIP )");
}
