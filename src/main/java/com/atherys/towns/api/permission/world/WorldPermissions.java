package com.atherys.towns.api.permission.world;

import com.atherys.towns.api.permission.Permission;

public final class WorldPermissions {

    public static final Permission BUILD = new WorldPermission("build", "Build");

    public static final Permission DESTROY = new WorldPermission("destroy", "Destroy");

    public static final Permission DAMAGE_NONPLAYERS = new WorldPermission("damage_nonplayers", "Damage Non-Players");

    public static final Permission DAMAGE_PLAYERS = new WorldPermission("damage_players", "Damage Players");

    public static final Permission INTERACT_DOORS = new WorldPermission("interact_doors", "Interact with Doors");

    public static final Permission INTERACT_REDSTONE = new WorldPermission("interact_redstone", "Interact with Redstone");

    public static final Permission INTERACT_CHESTS = new WorldPermission("interact_chests", "Interact with Chests");

    public static final Permission INTERACT_ENTITIES = new WorldPermission("interact_entities", "Interact with Entities");

}
