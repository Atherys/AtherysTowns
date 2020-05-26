package com.atherys.towns.permission;

import com.atherys.towns.AtherysTowns;
import com.atherys.towns.service.TownsPermissionService;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.context.ContextCalculator;
import org.spongepowered.api.service.permission.Subject;

import java.util.Set;

public class TownsContextCalculator implements ContextCalculator<Subject> {
    @Override
    public void accumulateContexts(Subject calculable, Set<Context> accumulator) {
        AtherysTowns.getInstance().getPermissionService().accumulateContexts(calculable, accumulator);
    }

    @Override
    public boolean matches(Context context, Subject calculable) {
        return TownsPermissionService.NATION_CONTEXT_KEY.equals(context.getKey()) || TownsPermissionService.TOWN_CONTEXT_KEY.equals(context.getKey());
    }
}
