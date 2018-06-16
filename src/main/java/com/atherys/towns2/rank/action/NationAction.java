package com.atherys.towns2.rank.action;

import org.spongepowered.api.util.annotation.CatalogedBy;

@CatalogedBy(NationActions.class)
public class NationAction extends Action {

    private static final String PERMISSION_TEMPLATE = "atherystowns.nation.${action}";

    NationAction(String id, String name) {
        super(id, name, PERMISSION_TEMPLATE.replace("${action}", id));
    }
}
