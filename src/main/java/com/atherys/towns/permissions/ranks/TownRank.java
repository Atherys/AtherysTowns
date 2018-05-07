package com.atherys.towns.permissions.ranks;

import com.atherys.towns.permissions.actions.TownsAction;
import java.util.List;
import org.spongepowered.api.CatalogType;
import org.spongepowered.api.util.annotation.CatalogedBy;

@CatalogedBy(TownRanks.class)
public class TownRank extends Rank implements CatalogType {

    private NationRank defaultNationRank;

    protected TownRank(String id, String name, List<? extends TownsAction> permittedActions,
        NationRank defaultNationRank, TownRank child) {
        super(id, name, permittedActions, child);
        this.defaultNationRank = defaultNationRank;
        TownRankRegistry.getInstance().add(this);
    }

    public NationRank getDefaultNationRank() {
        return defaultNationRank;
    }
}
