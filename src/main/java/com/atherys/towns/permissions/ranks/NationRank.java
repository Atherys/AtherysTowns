package com.atherys.towns.permissions.ranks;

import com.atherys.towns.permissions.actions.TownsAction;
import java.util.List;
import org.spongepowered.api.CatalogType;
import org.spongepowered.api.util.annotation.CatalogedBy;

@CatalogedBy(NationRanks.class)
public class NationRank extends Rank implements CatalogType {

    protected NationRank(String id, String name, List<? extends TownsAction> permittedActions,
        NationRank child) {
        super(id, name, permittedActions, child);
        NationRankRegistry.getInstance().add(this);
    }
}
