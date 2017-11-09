package com.atherys.towns.permissions.ranks;

import com.atherys.towns.permissions.actions.TownsAction;
import org.spongepowered.api.CatalogType;
import org.spongepowered.api.util.annotation.CatalogedBy;

import java.util.List;

@CatalogedBy( NationRanks.class )
public class NationRank extends Rank implements CatalogType {

    protected NationRank ( String id, String name, List<? extends TownsAction> permittedActions) {
        super(id, name, permittedActions);
        NationRankRegistry.getInstance().add(this);
    }

    @Override
    public String getId() {
        return super.getId();
    }

    @Override
    public String getName() {
        return super.getName();
    }
}
