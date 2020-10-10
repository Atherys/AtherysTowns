package com.atherys.towns.api.permission.town;

public final class TownPermissions {
    // permission to use town chat
    public static final TownPermission CHAT = new TownPermission("atherystowns.town.chat", "Use Town Chat");

    // permission to change town leader
    public static final TownPermission TRANSFER_LEADERSHIP = new TownPermission("atherystowns.town.leader", "Transfer Leadership");

    // permission to change town pvp policy
    public static final TownPermission SET_PVP = new TownPermission("atherystowns.town.pvp", "Set PvP");

    // permission to change town spawn
    public static final TownPermission SET_SPAWN = new TownPermission("atherystowns.town.setspawn", "Set Spawn");

    // permission to change whether the town is freely joinable or not
    public static final TownPermission SET_FREELY_JOINABLE = new TownPermission("atherystowns.town.joinable", "Set Freely Joinable");

    // permission to change the town color
    public static final TownPermission SET_COLOR = new TownPermission("atherystowns.town.color", "Set Color");

    // permission to change the town motd
    public static final TownPermission SET_MOTD = new TownPermission("atherystowns.town.motd", "Set Motd");

    // permission to change the town description
    public static final TownPermission SET_DESCRIPTION = new TownPermission("atherystowns.town.description", "Set Description");

    // permission to change the town name
    public static final TownPermission SET_NAME = new TownPermission("atherystowns.town.name", "Set Name");

    // permission to change the nation that the town belongs to
    public static final TownPermission JOIN_NATION = new TownPermission("atherystowns.town.nation", "Join Nation");

    // Permission to create a town without a nation
    public static final TownPermission CREATE_WITHOUT_NATION = new TownPermission("atherystowns.town.create_without_nation", "Create Town without a Nation");

    // Permission to create a town without a party
    public static final TownPermission CREATE_WITHOUT_PARTY = new TownPermission("atherystowns.town.create_without_party", "Create Town without a Party");

    // permission to deposit into the town bank
    public static final TownPermission DEPOSIT_INTO_BANK = new TownPermission("atherystowns.town.deposit", "Deposit");

    // permission to withdraw from the town bank
    public static final TownPermission WITHDRAW_FROM_BANK = new TownPermission("atherystowns.town.withdraw", "Withdraw");

    // permission to change permissions of other residents in the town
    public static final TownPermission SET_PERMISSION = new TownPermission("atherystowns.town.permission", "Set Permission");

    // permission to grant and revoke roles of other residents in the town
    public static final TownPermission SET_ROLE = new TownPermission("atherystowns.town.role", "Set Role");

    // permission to unclaim a plot belonging to the town
    public static final TownPermission UNCLAIM_PLOT = new TownPermission("atherystowns.plot.unclaim", "Unclaim Plot");

    // permission to claim a plot for the town
    public static final TownPermission CLAIM_PLOT = new TownPermission("atherystowns.plot.claim", "Claim Plot");

    // permission to rename plots belonging to the town
    public static final TownPermission RENAME_PLOT = new TownPermission("atherystowns.plot.rename", "Rename Plot");

    // permission to change the owner of a plot belonging to the town
    public static final TownPermission GRANT_PLOT = new TownPermission("atherystowns.plot.grant", "Grant Plot");

    // permission to kick residents from the town
    public static final TownPermission KICK_RESIDENT = new TownPermission("atherystowns.town.kick", "Kick Resident");

    // permission to invite residents to the town
    public static final TownPermission INVITE_RESIDENT = new TownPermission("atherystowns.town.invite", "Invite Resident");

    // permission to destroy the town
    public static final TownPermission RUIN_TOWN = new TownPermission("atherystowns.town.ruin", "Ruin Town");

    // permission to pay off taxes
    public static final TownPermission DEBT_PAY = new TownPermission("atherystowns.town.paydebt", "Pay back debt");

    // permission to start a raid on an enemy town
    public static final TownPermission START_RAID = new TownPermission("atherystowns.town.raid.start", "Start Raid");

    // permission to cancel a raid on an enemy town
    public static final TownPermission CANCEL_RAID = new TownPermission("atherystowns.town.raid.cancel", "Cancel Raid");
}
