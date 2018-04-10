package com.atherys.towns.permissions.ranks;

import com.atherys.towns.AtherysTowns;

public final class NationRanks {

    public static final NationRank NONE = new NationRank( "nation_none", "None", AtherysTowns.getConfig().TOWN.NATION_RANKS.NONE, null );

    public static final NationRank RESIDENT = new NationRank( "nation_resident", "Resident", AtherysTowns.getConfig().TOWN.NATION_RANKS.RESIDENT, NationRanks.NONE );

    public static final NationRank CO_LEADER = new NationRank( "nation_co_leader", "Co-Leader", AtherysTowns.getConfig().TOWN.NATION_RANKS.CO_LEADER, NationRanks.RESIDENT );

    public static final NationRank LEADER = new NationRank( "nation_leader", "Leader", AtherysTowns.getConfig().TOWN.NATION_RANKS.LEADER, NationRanks.CO_LEADER );
}
