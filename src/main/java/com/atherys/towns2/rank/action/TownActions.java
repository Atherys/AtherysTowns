package com.atherys.towns2.rank.action;

public final class TownActions {

    private static final TownAction CREATE_TOWN_RANK = new TownAction("rank-create", "Create Town Rank");

    private static final TownAction REMOVE_TOWN_RANK = new TownAction("rank-remove", "Remove Town Rank");

    private static final TownAction CLAIM_PLOT = new TownAction("plot-claim", "Claim Plot");

    private static final TownAction UNCLAIM_PLOT = new TownAction("plot-unclaim", "Unclaim Plot");

    private static final TownAction ADD_RESIDENT = new TownAction("resident-add", "Invite Resident");

    private static final TownAction REMOVE_RESIDENT = new TownAction("resident-remove", "Remove Resident");

    private static final TownAction BANK_WITHDRAW = new TownAction("bank-withdraw", "Withdraw From Town Bank");

    private static final TownAction BANK_DEPOSIT = new TownAction("bank-deposit", "Deposit Into Town Bank");

    private static final TownAction SET_NAME = new TownAction("set-name", "Set Town Name");

    private static final TownAction SET_DESCRIPTION = new TownAction("set-description", "Set Town Description");

    private static final TownAction SET_MOTD = new TownAction("set-motd", "Set Town MOTD");

    private static final TownAction SET_RANK = new TownAction("set-rank", "Set Resident's Rank");

    private static final TownAction SET_COLOR = new TownAction("set-color", "Set Town Color");

    private static final TownAction SET_TOWN_FLAGS = new TownAction("set-default-flags", "Set Default Town Flags");

    private static final TownAction SET_PLOT_FLAGS = new TownAction("set-plot-flags", "Set Plot Flags");

    private static final TownAction SET_PLOT_NAME = new TownAction("set-plot-name", "Set Plot Name");
}
