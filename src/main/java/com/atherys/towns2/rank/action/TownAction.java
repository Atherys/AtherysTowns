package com.atherys.towns2.rank.action;

import org.spongepowered.api.util.annotation.CatalogedBy;

@CatalogedBy(TownActions.class)
public class TownAction extends Action {

    private static final String PERMISSION_TEMPLATE = "atherystowns.town.${action}";

    TownAction(String id, String name) {
        super(id, name, PERMISSION_TEMPLATE.replace("${action}", id));
    }
}
