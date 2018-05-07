package com.atherys.towns.permissions.ranks;

import com.atherys.towns.AtherysTowns;

public final class TownRanks {

  public static final TownRank NONE = new TownRank("none", "None",
      AtherysTowns.getConfig().TOWN.TOWN_RANKS.NONE, NationRanks.RESIDENT, null);

  public static final TownRank RESIDENT = new TownRank("resident", "Resident",
      AtherysTowns.getConfig().TOWN.TOWN_RANKS.RESIDENT, NationRanks.RESIDENT, TownRanks.NONE);

  public static final TownRank CITIZEN = new TownRank("citizen", "Citizen",
      AtherysTowns.getConfig().TOWN.TOWN_RANKS.CITIZEN, NationRanks.RESIDENT, TownRanks.RESIDENT);

  public static final TownRank ASSISTANT = new TownRank("assistant", "Assistant",
      AtherysTowns.getConfig().TOWN.TOWN_RANKS.ASSISTANT, NationRanks.RESIDENT,
      TownRanks.CITIZEN);

  public static final TownRank CO_MAYOR = new TownRank("co_mayor", "Co-Mayor",
      AtherysTowns.getConfig().TOWN.TOWN_RANKS.CO_MAYOR, NationRanks.RESIDENT,
      TownRanks.ASSISTANT);

  public static final TownRank MAYOR = new TownRank("mayor", "Mayor",
      AtherysTowns.getConfig().TOWN.TOWN_RANKS.MAYOR, NationRanks.RESIDENT, TownRanks.CO_MAYOR);
}
