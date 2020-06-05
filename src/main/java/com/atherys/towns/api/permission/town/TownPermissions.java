package com.atherys.towns.api.permission.town;

import com.atherys.towns.api.permission.Permission;

public final class TownPermissions {
    // permission to use town chat
    public static final TownPermission CHAT = new TownPermission("atherystowns.town.chat", "Use Town Chat");

    // permission to change town leader
    public static final TownPermission TRANSFER_LEADERSHIP = new TownPermission("atherystowns.town.set.leader", "Transfer Leadership");

    // permission to change town pvp policy
    public static final TownPermission SET_PVP = new TownPermission("atherystowns.town.set.pvp", "Set PvP");

    // permission to change town spawn
    public static final TownPermission SET_SPAWN = new TownPermission("atherystowns.town.set.spawn", "Set Spawn");

    // permission to change whether the town is freely joinable or not
    public static final TownPermission SET_FREELY_JOINABLE = new TownPermission("atherystowns.town.set.joinable", "Set Freely Joinable");

    // permission to change the town color
    public static final TownPermission SET_COLOR = new TownPermission("atherystowns.town.set.color", "Set Color");

    // permission to change the town motd
    public static final TownPermission SET_MOTD = new TownPermission("atherystowns.town.set.motd", "Set Motd");

    // permission to change the town description
    public static final TownPermission SET_DESCRIPTION = new TownPermission("atherystowns.town.set.description", "Set Description");

    // permission to change the town name
    public static final TownPermission SET_NAME = new TownPermission("atherystowns.town.set.name", "Set Name");

    // permission to change the nation that the town belongs to
    public static final TownPermission JOIN_NATION = new TownPermission("atherystowns.town.set.nation", "Join Nation");

    // permission to deposit into the town bank
    public static final TownPermission DEPOSIT_INTO_BANK = new TownPermission("atherystowns.town.deposit", "Deposit");

    // permission to withdraw from the town bank
    public static final TownPermission WITHDRAW_FROM_BANK = new TownPermission("atherystowns.town.withdraw", "Withdraw");

    // permission to change permissions of other residents in the town
    public static final TownPermission SET_PERMISSION = new TownPermission("atherystowns.town.set.permission", "Set Permission");

    // permission to grant and revoke roles of other residents in the town
    public static final TownPermission SET_ROLE = new TownPermission("atherystowns.town.set.role", "Set Role");

    // permission to unclaim a plot belonging to the town
    public static final TownPermission UNCLAIM_PLOT = new TownPermission("atherystowns.town.plot.unclaim", "Unclaim Plot");

    // permission to claim a plot for the town
    public static final TownPermission CLAIM_PLOT = new TownPermission("atherystowns.town.plot.claim", "Claim Plot");

    // permission to rename plots belonging to the town
    public static final TownPermission RENAME_PLOT = new TownPermission("atherystowns.town.plot.rename", "Rename Plot");

    // permission to change the owner of a plot belonging to the town
    public static final TownPermission GRANT_PLOT = new TownPermission("atherystowns.town.plot.grant", "Grant Plot");

    // permission to kick residents from the town
    public static final TownPermission KICK_RESIDENT = new TownPermission("atherystowns.town.resident.kick", "Kick Resident");

    // permission to invite residents to the town
    public static final TownPermission INVITE_RESIDENT = new TownPermission("atherystowns.town.resident.invite", "Invite Resident");
}
