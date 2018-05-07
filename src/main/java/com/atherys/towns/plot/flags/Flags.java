package com.atherys.towns.plot.flags;

import com.atherys.towns.permissions.actions.TownActions;

public final class Flags {

    public static final Flag PVP = new Flag("pvp", "PvP", TownActions.SET_FLAG_PVP, Extents.ANY,
        Extents.NONE);

    public static final Flag BUILD = new Flag("build", "Build", TownActions.SET_FLAG_BUILD,
        Extents.ANY, Extents.NATION, Extents.TOWN, Extents.NONE);

    public static final Flag DESTROY = new Flag("destroy", "Destroy", TownActions.SET_FLAG_DESTROY,
        Extents.ANY, Extents.NATION, Extents.TOWN, Extents.NONE);

    public static final Flag SWITCH = new Flag("switch", "Open/Switch", TownActions.SET_FLAG_SWITCH,
        Extents.ANY, Extents.NATION, Extents.TOWN, Extents.NONE);

    public static final Flag DAMAGE_ENTITY = new Flag("dmg_entity", "Damage Entity",
        TownActions.SET_FLAG_DAMAGE_ENTITY, Extents.ANY, Extents.NATION, Extents.TOWN,
        Extents.NONE);

    public static final Flag JOIN = new Flag("join", "Join", TownActions.SET_FLAG_JOIN, Extents.ANY,
        Extents.NONE);

}
