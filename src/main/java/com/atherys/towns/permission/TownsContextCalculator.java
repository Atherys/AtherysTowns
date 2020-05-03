package com.atherys.towns.permission;

import com.atherys.towns.service.PermissionService;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.context.ContextCalculator;

import java.util.Set;

@Singleton
public class TownsContextCalculator implements ContextCalculator<Player> {

    @Inject
    private PermissionService permissionService;

    public TownsContextCalculator() {
    }

    @Override
    public void accumulateContexts(Player calculable, Set<Context> accumulator) {
        permissionService.accumulateContexts(calculable, accumulator);
    }

    @Override
    public boolean matches(Context context, Player calculable) {
        return PermissionService.NATION_CONTEXT_KEY.equals(context.getKey()) || PermissionService.TOWN_CONTEXT_KEY.equals(context.getKey());
    }
}
