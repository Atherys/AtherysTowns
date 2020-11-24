package com.atherys.towns.api.permission.nation;

public final class NationPermissions {
    // permission to use nation chat
    public static final NationPermission CHAT = new NationPermission("atherystowns.nation.chat", "Nation Chat");

    // permission to set the leader of the nation
    public static final NationPermission TRANSFER_LEADERSHIP = new NationPermission("atherystowns.nation.leader", "Transfer Leadership");

    // permission to set the nation to joinable
    public static final NationPermission SET_JOINABLE = new NationPermission("atherystowns.nation.joinable", "Set Joinable");

    public static final NationPermission ADD_ALLY = new NationPermission("atherystowns.nation.add_ally", "Add Ally");

    public static final NationPermission ADD_NEUTRAL = new NationPermission("atherystowns.nation.add_neutral", "Add Neutral");

    public static final NationPermission ADD_ENEMY = new NationPermission("atherystowns.nation.add_enemy", "Add Enemy");

    // permission to set the nation description
    public static final NationPermission SET_DESCRIPTION = new NationPermission("atherystowns.nation.description", "Set Description");

    // permission to rename the nation
    public static final NationPermission SET_NAME = new NationPermission("atherystowns.nation.name", "Set Name");

    // permission to set the nation's tax
    public static final NationPermission SET_TAX = new NationPermission("atherystowns.nation.tax", "Set Tax");

    // permission to set the nation's tax
    public static final NationPermission SET_COLOR = new NationPermission("atherystowns.nation.color", "Set Color");

    // permission to invite a town to the nation
    public static final NationPermission INVITE_TOWN = new NationPermission("atherystowns.nation.invite", "Invite Town");

    // permission to kick a town from the nation
    public static final NationPermission KICK_TOWN = new NationPermission("atherystowns.nation.kick", "Remove Town");

    // permission to deposit into nation bank
    public static final NationPermission DEPOSIT_INTO_BANK = new NationPermission("atherystowns.nation.deposit", "Deposit Currency");

    // permission to withdraw from town bank
    public static final NationPermission WITHDRAW_FROM_BANK = new NationPermission("atherystowns.nation.withdraw", "Withdraw Currency");

    // permission to change the permissions of other residents in the nation
    public static final NationPermission SET_PERMISSION = new NationPermission("atherystowns.nation.permission", "Revoke Permission");

    // permission to change the roles of other residents in the nation
    public static final NationPermission SET_ROLE = new NationPermission("atherystowns.nation.role", "Revoke Permission");

    // permission to set the capital of a nation
    public static final NationPermission SET_CAPITAL = new NationPermission("atherystowns.nation.capital", "Set Capital");
}
