package com.atherys.towns2.rank;

import com.atherys.towns2.rank.action.NationAction;
import javax.annotation.Nonnull;
import org.spongepowered.api.service.context.Context;

public class NationRank extends Rank<NationAction> {

    public NationRank(String id, String name) {
        super(id, name);
    }
}
