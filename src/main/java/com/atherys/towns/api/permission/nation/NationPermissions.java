package com.atherys.towns.api.permission.nation;

import com.atherys.towns.api.permission.Permission;

public final class NationPermissions {
    public static final NationPermission CHAT = new NationPermission("chat", "Nation Chat");

    public static final NationPermission TRANSFER_LEADERSHIP = new NationPermission("transfer_leadership", "Transfer Leadership");

    public static final NationPermission DECLARE_PEACE = new NationPermission("declare_peace", "Declare Peace");

    public static final NationPermission DECLARE_WAR = new NationPermission("declare_war", "Declare War");

    public static final NationPermission ADD_ALLY = new NationPermission("add_ally", "Add Ally");

    public static final NationPermission ADD_NEUTRAL = new NationPermission("add_neutral", "Add Neutral");

    public static final NationPermission ADD_ENEMY = new NationPermission("add_enemy", "Add Enemy");

    public static final NationPermission SET_FREELY_JOINABLE = new NationPermission("set_freely_joinable", "Set Freely Joinable");

    public static final NationPermission SET_DESCRIPTION = new NationPermission("set_description", "Set Description");

    public static final NationPermission SET_NAME = new NationPermission("set_name", "Set Name");

    public static final NationPermission DEPOSIT_INTO_BANK = new NationPermission("deposit", "Deposit Currency");

    public static final NationPermission WITHDRAW_FROM_BANK = new NationPermission("withdraw", "Withdraw Currency");

    public static final NationPermission REVOKE_PERMISSION = new NationPermission("revoke_permission", "Revoke Permission");

    public static final NationPermission ADD_PERMISSION = new NationPermission("add_permission", "Add Permission");

    public static final NationPermission KICK_TOWN = new NationPermission("kick_town", "Kick Town");

    public static final NationPermission INVITE_TOWN = new NationPermission("invite_town", "Invite Town");

    public static final NationPermission SET_CAPITAL = new NationPermission("set_capital", "Set Capital");
}
