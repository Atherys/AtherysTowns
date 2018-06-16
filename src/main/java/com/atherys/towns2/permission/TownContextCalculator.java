package com.atherys.towns2.permission;

import java.util.Set;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.context.ContextCalculator;
import org.spongepowered.api.service.permission.Subject;

public class TownContextCalculator implements ContextCalculator<Subject> {

    @Override
    public void accumulateContexts(Subject calculable, Set<Context> accumulator) {
        calculable.getCommandSource().ifPresent(cmdSource -> {
            if (cmdSource instanceof Player) {
                Player player = (Player) cmdSource;

                
            }
        });
    }

    @Override
    public boolean matches(Context context, Subject calculable) {
        return false;
    }
}
