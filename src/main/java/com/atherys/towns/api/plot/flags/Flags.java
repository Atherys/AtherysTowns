package com.atherys.towns.api.plot.flags;

public final class Flags {

    public static final Flag PVP = new Flag("pvp", "PvP", "atheryscore.flag.town.${town}.pvp", Extents.ANY, Extents.NONE);

    public static final Flag BUILD = new Flag("build", "Build", "atheryscore.flag.town.${town}.build", Extents.ANY, Extents.NATION, Extents.TOWN, Extents.NONE);

    public static final Flag DESTROY = new Flag("destroy", "Destroy", "atheryscore.flag.town.${town}.destroy", Extents.ANY, Extents.NATION, Extents.TOWN, Extents.NONE);

    public static final Flag SWITCH = new Flag("switch", "Open/Switch", "atheryscore.flag.town.${town}.switch", Extents.ANY, Extents.NATION, Extents.TOWN, Extents.NONE);

    public static final Flag DAMAGE_ENTITY = new Flag("dmg_entity", "Damage Entity", "atheryscore.flag.town.${town}.damage_entity", Extents.ANY, Extents.NATION, Extents.TOWN, Extents.NONE);

    public static final Flag JOIN = new Flag("join", "Join", "atheryscore.flag.town.${town}.join", Extents.ANY, Extents.NONE);

}
