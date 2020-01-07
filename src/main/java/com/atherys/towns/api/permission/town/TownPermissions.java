package com.atherys.towns.api.permission.town;

import com.atherys.towns.api.permission.Permission;

public final class TownPermissions {
    public static final TownPermission CHAT = new TownPermission("chat", "Use Town Chat");

    public static final TownPermission TRANSFER_LEADERSHIP = new TownPermission("transfer_leadership", "Transfer Leadership");

    public static final TownPermission SET_PVP = new TownPermission("set_pvp", "Set PvP");

    public static final TownPermission SET_SPAWN = new TownPermission("set_spawn", "Set Spawn");

    public static final TownPermission SET_FREELY_JOINABLE = new TownPermission("set_freely_joinable", "Set Freely Joinable");

    public static final TownPermission SET_COLOR = new TownPermission("set_color", "Set Color");

    public static final TownPermission SET_MOTD = new TownPermission("set_motd", "Set Motd");

    public static final TownPermission SET_DESCRIPTION = new TownPermission("set_description", "Set Description");

    public static final TownPermission SET_NAME = new TownPermission("set_name", "Set Name");

    public static final TownPermission JOIN_NATION = new TownPermission("join_nation", "Join Nation");

    public static final TownPermission LEAVE_NATION = new TownPermission("leave_nation", "Leave Nation");

    public static final TownPermission DEPOSIT_INTO_BANK = new TownPermission("deposit", "Deposit");

    public static final TownPermission WITHDRAW_FROM_BANK = new TownPermission("withdraw", "Withdraw");

    public static final TownPermission REVOKE_PERMISSION = new TownPermission("revoke_permission", "Revoke Permission");

    public static final TownPermission ADD_PERMISSION = new TownPermission("add_permission", "Add Permission");

    public static final TownPermission UNCLAIM_PLOT = new TownPermission("unclaim_plot", "Unclaim Plot");

    public static final TownPermission CLAIM_PLOT = new TownPermission("claim_plot", "Claim Plot");

    public static final TownPermission RENAME_PLOT = new TownPermission("rename_plot", "Rename Plot");

    public static final TownPermission GRANT_PLOT = new TownPermission("grant_plot", "Grant Plot");

    public static final TownPermission KICK_RESIDENT = new TownPermission("kick_resident", "Kick Resident");

    public static final TownPermission INVITE_RESIDENT = new TownPermission("invite_resident", "Invite Resident");
}
