package com.atherys.towns.permissions.ranks;

import com.atherys.towns.permissions.actions.NationActions;

import java.util.Arrays;

public final class NationRanks {

    public static final NationRank LEADER = new NationRank( "leader", "Leader", Arrays.asList(
            NationActions.CHAT,
            NationActions.NATION_DEPOSIT,
            NationActions.NATION_WITHDRAW,
            NationActions.SET_COLOR,
            NationActions.SET_DESCRIPTION,
            NationActions.SET_NAME,
            NationActions.SET_RANK,
            NationActions.SET_LEADER_TITLE
    ));

    public static final NationRank CO_LEADER = new NationRank( "leader", "Co-Leader", Arrays.asList(
            NationActions.CHAT,
            NationActions.NATION_DEPOSIT,
            NationActions.NATION_WITHDRAW
    ));

    public static final NationRank RESIDENT = new NationRank( "resident", "Resident", Arrays.asList(
            NationActions.CHAT,
            NationActions.NATION_DEPOSIT
    ));

}
