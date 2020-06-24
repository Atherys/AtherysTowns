package com.atherys.towns.api.permission.nation;

public final class NationPermissions {
    // permission to use nation chat
    public static final NationPermission CHAT = new NationPermission("atherystowns.nation.chat", "Nation Chat");

    // permission to invite a town to the nation
    public static final NationPermission INVITE_TOWN = new NationPermission("atherystowns.nation.invite", "Declare War");

    // permission to kick a town from the nation
    public static final NationPermission KICK_TOWN = new NationPermission("atherystowns.nation.kick", "Declare War");

    // permission to deposit into nation bank
    public static final NationPermission DEPOSIT_INTO_BANK = new NationPermission("atherystowns.nation.deposit", "Deposit Currency");

    // permission to withdraw from town bank
    public static final NationPermission WITHDRAW_FROM_BANK = new NationPermission("atherystowns.nation.withdraw", "Withdraw Currency");

    // permission to change the permissions of other residents in the nation
    public static final NationPermission SET_PERMISSION = new NationPermission("atherystowns.nation.permission", "Revoke Permission");

    // permission to change the roles of other residents in the nation
    public static final NationPermission SET_ROLE = new NationPermission("atherystowns.nation.role", "Revoke Permission");
}
