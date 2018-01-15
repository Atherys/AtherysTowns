package com.atherys.towns.permissions.ranks;

import com.atherys.towns.permissions.actions.TownActions;

import java.util.Arrays;

public final class TownRanks {

    public static final TownRank NONE = new TownRank ( "none", "None", Arrays.asList(
            TownActions.CREATE_TOWN,
            TownActions.JOIN_TOWN
    ), NationRanks.RESIDENT, null);

    public static final TownRank RESIDENT = new TownRank ( "resident", "Resident", Arrays.asList(
            TownActions.CHAT,
            TownActions.JOIN_TOWN,
            TownActions.LEAVE_TOWN,
            TownActions.TOWN_DEPOSIT
    ), NationRanks.RESIDENT, TownRanks.NONE);

    public static final TownRank CITIZEN = new TownRank ( "citizen", "Citizen", Arrays.asList(
            TownActions.CHAT,
            TownActions.JOIN_TOWN,
            TownActions.LEAVE_TOWN,
            TownActions.TOWN_DEPOSIT
    ), NationRanks.RESIDENT, TownRanks.RESIDENT);

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
    ), NationRanks.RESIDENT, TownRanks.CITIZEN);

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
    ), NationRanks.RESIDENT, TownRanks.ASSISTANT);

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
    ), NationRanks.RESIDENT, TownRanks.CO_MAYOR );
}
