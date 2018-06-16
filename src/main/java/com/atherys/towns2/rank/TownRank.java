package com.atherys.towns2.rank;

import com.atherys.towns2.rank.action.TownAction;
import javax.annotation.Nonnull;
import org.spongepowered.api.service.context.Context;

public class TownRank extends Rank<TownAction> {

    private Context context;

    public TownRank(String id, String name) {
        super(id, name);
        context = new Context("atherystowns-town-rank", id);
    }

    @Override
    @Nonnull
    public Context getContext() {
        return context;
    }
}
