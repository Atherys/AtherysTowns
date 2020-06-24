package com.atherys.towns.listener;

import com.atherys.towns.facade.PlotFacade;
import com.atherys.towns.facade.ResidentFacade;
import com.atherys.towns.facade.TownSpawnFacade;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.network.ClientConnectionEvent;

@Singleton
public class PlayerListener {

    @Inject
    private PlotFacade plotFacade;

    @Inject
    private TownSpawnFacade townSpawnFacade;

    @Inject
    private ResidentFacade residentFacade;

    @Listener
    public void onPlayerMove(MoveEntityEvent event, @Root Player player) {
        // And the move event was triggered due to a change in block position
        if (!event.getFromTransform().getPosition().toInt().equals(event.getToTransform().getPosition().toInt())) {
            plotFacade.onPlayerMove(event.getFromTransform(), event.getToTransform(), (Player) event.getTargetEntity());
            townSpawnFacade.onPlayerMove(player);
        }
    }

    @Listener
    public void onPlayerLogin(ClientConnectionEvent.Join event, @Root Player player) {
        residentFacade.onLogin(player);
    }
}
