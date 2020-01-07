package com.atherys.towns.api.permission.world;

import com.atherys.towns.api.permission.Permission;

public final class WorldPermissions {

    public static final WorldPermission BUILD = new WorldPermission("build", "Build");

    public static final WorldPermission DESTROY = new WorldPermission("destroy", "Destroy");

    public static final WorldPermission DAMAGE_NONPLAYERS = new WorldPermission("damage_nonplayers", "Damage Non-Players");

    public static final WorldPermission DAMAGE_PLAYERS = new WorldPermission("damage_players", "Damage Players");

    public static final WorldPermission INTERACT_DOORS = new WorldPermission("interact_doors", "Interact with Doors");

    public static final WorldPermission INTERACT_REDSTONE = new WorldPermission("interact_redstone", "Interact with Redstone");

    public static final WorldPermission INTERACT_CHESTS = new WorldPermission("interact_chests", "Interact with Chests");

    public static final WorldPermission INTERACT_ENTITIES = new WorldPermission("interact_entities", "Interact with Entities");

}
