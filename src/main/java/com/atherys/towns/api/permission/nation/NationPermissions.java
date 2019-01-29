package com.atherys.towns.api.permission.nation;

import com.atherys.towns.api.permission.Permission;

public final class NationPermissions {
    public static final Permission CHAT = new NationPermission("chat", "Nation Chat");

    public static final Permission TRANSFER_LEADERSHIP = new NationPermission("transfer_leadership", "Transfer Leadership");

    public static final Permission DECLARE_PEACE = new NationPermission("declare_peace", "Declare Peace");

    public static final Permission DECLARE_WAR = new NationPermission("declare_war", "Declare War");

    public static final Permission REMOVE_ALLY = new NationPermission("remove_ally", "Remove Ally");

    public static final Permission ADD_ALLY = new NationPermission("add_ally", "Add Ally");

    public static final Permission SET_FREELY_JOINABLE = new NationPermission("set_freely_joinable", "Set Freely Joinable");

    public static final Permission SET_DESCRIPTION = new NationPermission("set_description", "Set Description");

    public static final Permission SET_NAME = new NationPermission("set_name", "Set Name");

    public static final Permission DEPOSIT_INTO_BANK = new NationPermission("deposit", "Deposit Currency");

    public static final Permission WITHDRAW_FROM_BANK = new NationPermission("withdraw", "Withdraw Currency");

    public static final Permission REVOKE_PERMISSION = new NationPermission("revoke_permission", "Revoke Permission");

    public static final Permission ADD_PERMISSION = new NationPermission("add_permission", "Add Permission");

    public static final Permission KICK_TOWN = new NationPermission("kick_town", "Kick Town");

    public static final Permission INVITE_TOWN = new NationPermission("invite_town", "Invite Town");
}
