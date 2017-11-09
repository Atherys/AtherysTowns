package com.atherys.towns.permissions.ranks;

import com.atherys.towns.permissions.actions.TownActions;

import java.util.Arrays;

/*
    NONE = [
      "NULL"
      "CHAT"
      "JOIN_TOWN"
      "LEAVE_TOWN"
    ]
    RESIDENT = [
      "NULL",
      "CHAT",
      "JOIN_TOWN",
      "LEAVE_TOWN",
      "TOWN_DEPOSIT",
      "NATION_DEPOSIT"
    ]
    CITIZEN = [
      "NULL",
      "CHAT",
      "JOIN_TOWN",
      "LEAVE_TOWN",
      "TOWN_DEPOSIT",
      "NATION_DEPOSIT"
    ]
    ASSISTANT = [
      "NULL",
      "CHAT",
      "JOIN_TOWN",
      "LEAVE_TOWN",
      "INVITE_PLAYER",
      "KICK_PLAYER",
      "SET_MOTD",
      "SET_DESCRIPTION",
      "SET_COLOR",
      "SHOW_TOWN_BORDER",
      "TOWN_DEPOSIT",
      "NATION_DEPOSIT"
    ]
    CO_MAYOR = [
      "NULL",
      "CHAT",
      "INVITE_PLAYER",
      "KICK_PLAYER",
      "LEAVE_TOWN",
      "SET_MOTD",
      "SET_DESCRIPTION",
      "SET_COLOR",
      "SET_NAME",
      "SET_RANK",
      "CLAIM_PLOT",
      "UNCLAIM_PLOT",
      "SET_FLAGS",
      "SET_FLAG_PVP",
      "SHOW_TOWN_BORDER",
      "TOWN_DEPOSIT",
      "TOWN_WITHDRAW",
      "NATION_DEPOSIT",
      "MODIFY_PLOT_FLAG",
      "MODIFY_PLOT_NAME"
    ]
    MAYOR = [
      "NULL",
      "CHAT",
      "INVITE_PLAYER",
      "KICK_PLAYER",
      "SET_MOTD",
      "SET_DESCRIPTION",
      "SET_COLOR",
      "CLAIM_PLOT",
      "UNCLAIM_PLOT",
      "SET_NAME",
      "SET_RANK",
      "SET_MAYOR",
      "SET_FLAGS",
      "SET_FLAG_PVP",
      "SET_FLAG_BUILD",
      "SET_FLAG_DESTROY",
      "SET_FLAG_JOIN",
      "SET_FLAG_SWITCH",
      "SET_FLAG_DAMAGE_ENTITY",
      "RUIN_TOWN",
      "SHOW_TOWN_BORDER",
      "TOWN_DEPOSIT",
      "TOWN_WITHDRAW",
      "NATION_DEPOSIT",
      "MODIFY_PLOT_FLAG",
      "MODIFY_PLOT_NAME"
    ]
 */

public final class TownRanks {
    public static final TownRank MAYOR = new TownRank ( "mayor", "Mayor", Arrays.asList(
            TownActions.CHAT,
            TownActions.INVITE_PLAYER,
            TownActions.KICK_PLAYER,
            TownActions.SET_MOTD,
            TownActions.SET_DESCRIPTION,
            TownActions.SET_COLOR,
            TownActions.CLAIM_PLOT,
            TownActions.UNCLAIM_PLOT,
            TownActions.SET_NAME,
            TownActions.SET_RANK,
            TownActions.SET_MAYOR,
            TownActions.SET_FLAGS,
            TownActions.SET_FLAG_PVP,
            TownActions.SET_FLAG_BUILD,
            TownActions.SET_FLAG_DESTROY,
            TownActions.SET_FLAG_JOIN,
            TownActions.SET_FLAG_SWITCH,
            TownActions.SET_FLAG_DAMAGE_ENTITY,
            TownActions.RUIN_TOWN,
            TownActions.SHOW_TOWN_BORDER,
            TownActions.TOWN_DEPOSIT,
            TownActions.TOWN_WITHDRAW,
            TownActions.MODIFY_PLOT_FLAG,
            TownActions.MODIFY_PLOT_NAME
    ), NationRanks.RESIDENT);

    public static final TownRank CO_MAYOR = new TownRank ( "co_mayor", "Co-Mayor", Arrays.asList(
            TownActions.CHAT,
            TownActions.INVITE_PLAYER,
            TownActions.KICK_PLAYER,
            TownActions.LEAVE_TOWN,
            TownActions.SET_MOTD,
            TownActions.SET_DESCRIPTION,
            TownActions.SET_COLOR,
            TownActions.SET_NAME,
            TownActions.SET_RANK,
            TownActions.CLAIM_PLOT,
            TownActions.UNCLAIM_PLOT,
            TownActions.SET_FLAGS,
            TownActions.SET_FLAG_PVP,
            TownActions.SHOW_TOWN_BORDER,
            TownActions.TOWN_DEPOSIT,
            TownActions.TOWN_WITHDRAW,
            TownActions.MODIFY_PLOT_FLAG,
            TownActions.MODIFY_PLOT_NAME
    ), NationRanks.RESIDENT);

    public static final TownRank ASSISTANT = new TownRank ( "assistant", "Assistant", Arrays.asList(
            TownActions.CHAT,
            TownActions.JOIN_TOWN,
            TownActions.LEAVE_TOWN,
            TownActions.INVITE_PLAYER,
            TownActions.KICK_PLAYER,
            TownActions.SET_MOTD,
            TownActions.SET_DESCRIPTION,
            TownActions.SET_COLOR,
            TownActions.SHOW_TOWN_BORDER,
            TownActions.TOWN_DEPOSIT
    ), NationRanks.RESIDENT);

    public static final TownRank CITIZEN = new TownRank ( "citizen", "Citizen", Arrays.asList(
            TownActions.CHAT,
            TownActions.JOIN_TOWN,
            TownActions.LEAVE_TOWN,
            TownActions.TOWN_DEPOSIT
    ), NationRanks.RESIDENT);

    public static final TownRank RESIDENT = new TownRank ( "resident", "Resident", Arrays.asList(
            TownActions.CHAT,
            TownActions.JOIN_TOWN,
            TownActions.LEAVE_TOWN,
            TownActions.TOWN_DEPOSIT
    ), NationRanks.RESIDENT);

    public static final TownRank NONE = new TownRank ( "none", "None", Arrays.asList(
            TownActions.CREATE_TOWN,
            TownActions.JOIN_TOWN
    ), NationRanks.RESIDENT);
}
