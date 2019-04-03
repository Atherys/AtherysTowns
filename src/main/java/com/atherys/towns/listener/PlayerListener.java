package com.atherys.towns.listener;

import com.atherys.towns.facade.PlotFacade;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.MoveEntityEvent;

@Singleton
public class PlayerListener {

    @Inject
    private PlotFacade plotFacade;

    @Listener
    public void onPlayerMove(MoveEntityEvent event) {
        // If the entity that moved was a player
        if (event.getTargetEntity() instanceof Player) {
            // And the move event was triggered due to a change in block position
            if (!event.getFromTransform().getPosition().toInt().equals(event.getToTransform().getPosition().toInt())) {
                plotFacade.onPlayerMove(event.getFromTransform(), event.getToTransform(), (Player) event.getTargetEntity());
            }
        }
    }

}
