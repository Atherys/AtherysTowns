package com.atherys.towns2.rank;

import com.atherys.towns2.rank.action.TownAction;
import com.atherys.towns2.town.Town;

public class TownRank extends Rank<TownAction> {
    public TownRank(Town town, String id, String name) {
        super(town.getUniqueId() + "." + id, name);
    }
}
