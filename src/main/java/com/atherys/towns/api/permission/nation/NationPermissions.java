package com.atherys.towns.api.permission.nation;

import com.atherys.towns.api.permission.Permission;

public final class NationPermissions {
    // permission to use nation chat
    public static final NationPermission CHAT = new NationPermission("atherystowns.nation.chat", "Nation Chat");

    // permission to set nation leader
    public static final NationPermission TRANSFER_LEADERSHIP = new NationPermission("atherystowns.nation.set.leader", "Transfer Leadership");

    // permission to declare peace
    public static final NationPermission DECLARE_PEACE = new NationPermission("atherystowns.nation.declare.peace", "Declare Peace");

    // permission to declare war
    public static final NationPermission DECLARE_WAR = new NationPermission("atherystowns.nation.declare.war", "Declare War");

    // permission to invite a town to the nation
    public static final NationPermission INVITE_TOWN = new NationPermission("atherystowns.nation.town.invite", "Declare War");

    // permission to kick a town from the nation
    public static final NationPermission KICK_TOWN = new NationPermission("atherystowns.nation.town.kick", "Declare War");

    // permission to add another nation as an ally
    public static final NationPermission ADD_ALLY = new NationPermission("atherystowns.nation.add.ally", "Add Ally");

    // permission to set another nation as neutral
    public static final NationPermission ADD_NEUTRAL = new NationPermission("atherystowns.nation.add.neutral", "Add Neutral");

    // permission to add another nation as an enemy
    public static final NationPermission ADD_ENEMY = new NationPermission("atherystowns.nation.add.enemy", "Add Enemy");

    // permission to set nation as freely joinable
    public static final NationPermission SET_FREELY_JOINABLE = new NationPermission("atherystowns.nation.set.joinable", "Set Freely Joinable");

    // permission to change nation description
    public static final NationPermission SET_DESCRIPTION = new NationPermission("atherystowns.nation.set.description", "Set Description");

    // permission to change nation name
    public static final NationPermission SET_NAME = new NationPermission("atherystowns.nation.set.name", "Set Name");

    // permission to deposit into nation bank
    public static final NationPermission DEPOSIT_INTO_BANK = new NationPermission("atherystowns.nation.deposit", "Deposit Currency");

    // permission to withdraw from town bank
    public static final NationPermission WITHDRAW_FROM_BANK = new NationPermission("atherystowns.nation.withdraw", "Withdraw Currency");

    // permission to change the permissions of other residents in the nation
    public static final NationPermission SET_PERMISSION = new NationPermission("atherystowns.nation.set.permission", "Revoke Permission");

    // permission to change the capital of the nation to another town
    public static final NationPermission SET_CAPITAL = new NationPermission("atherystowns.nation.set.capital", "Set Capital");
}
