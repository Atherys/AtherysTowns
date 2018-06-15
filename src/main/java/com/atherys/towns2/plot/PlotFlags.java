package com.atherys.towns2.plot;

import me.ryanhamshire.griefprevention.api.claim.ClaimFlag;

public final class PlotFlags {

    public static final PlotFlag BLOCK_BREAK = new PlotFlag(
            "atherys:block_break",
            "Break Block",
            ClaimFlag.BLOCK_BREAK
    );

    public static final PlotFlag BLOCK_PLACE = new PlotFlag(
            "atherys:block_place",
            "Place Block",
            ClaimFlag.BLOCK_PLACE
    );

    public static final PlotFlag ENTER = new PlotFlag(
            "atherys:enter",
            "Enter",
            ClaimFlag.ENTER_CLAIM
    );

    public static final PlotFlag OPEN_CONTAINER = new PlotFlag(
            "atherys:open_container",
            "Open Container",
            ClaimFlag.INTERACT_INVENTORY
    );

    public static final PlotFlag DAMAGE_ENTITY = new PlotFlag(
            "atherys:damage_entity",
            "Damage Entity",
            ClaimFlag.ENTITY_DAMAGE
    );

}
