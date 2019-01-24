package com.atherys.towns.api.permission.town;

import com.atherys.towns.api.permission.Permission;

public final class TownPermissions {
    public static final Permission CHAT = new Permission("town_chat", "Use Town Chat");

    public static final Permission TRANSFER_LEADERSHIP = new TownPermission("transfer_leadership", "Transfer Leadership");

    public static final Permission SET_PVP = new TownPermission("set_pvp", "Set PvP");

    public static final Permission SET_SPAWN = new TownPermission("set_spawn", "Set Spawn");

    public static final Permission SET_FREELY_JOINABLE = new TownPermission("set_freely_joinable", "Set Freely Joinable");

    public static final Permission SET_COLOR = new TownPermission("set_color", "Set Color");

    public static final Permission SET_MOTD = new TownPermission("set_motd", "Set Motd");

    public static final Permission SET_DESCRIPTION = new TownPermission("set_description", "Set Description");

    public static final Permission SET_NAME = new TownPermission("set_name", "Set Name");

    public static final Permission JOIN_NATION = new TownPermission("join_nation", "Join Nation");

    public static final Permission LEAVE_NATION = new TownPermission("leave_nation", "Leave Nation");

    public static final Permission DEPOSIT_INTO_BANK = new TownPermission("deposit", "Deposit");

    public static final Permission WITHDRAW_FROM_BANK = new TownPermission("withdraw", "Withdraw");

    public static final Permission REVOKE_PERMISSION = new TownPermission("revoke_permission", "Revoke Permission");

    public static final Permission ADD_PERMISSION = new TownPermission("add_permission", "Add Permission");

    public static final Permission UNCLAIM_PLOT = new TownPermission("unclaim_plot", "Unclaim Plot");

    public static final Permission CLAIM_PLOT = new TownPermission("claim_plot", "Claim Plot");

    public static final Permission KICK_RESIDENT = new TownPermission("kick_resident", "Kick Resident");

    public static final Permission INVITE_RESIDENT = new TownPermission("invite_resident", "Invite Resident");
}
