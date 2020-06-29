package com.atherys.towns.listener;

import com.atherys.core.utils.EntityUtils;
import com.atherys.towns.TownsConfig;
import com.atherys.towns.api.event.PlayerVoteEvent;
import com.atherys.towns.facade.*;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.entity.SpawnEntityEvent;
import org.spongepowered.api.event.filter.Getter;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.network.ClientConnectionEvent;

@Singleton
public class PlayerListener {

    @Inject
    private PlotFacade plotFacade;

    @Inject
    private TownSpawnFacade townSpawnFacade;

    @Inject
    private TownFacade townFacade;

    @Inject
    private TownsConfig config;

    @Inject
    private ResidentFacade residentFacade;

    @Inject
    private PollFacade pollFacade;

    @Inject
    private TownsMessagingFacade townsMsg;

    @Listener
    public void onPlayerMove(MoveEntityEvent event, @Root Player player) {
        // And the move event was triggered due to a change in block position
        if (!event.getFromTransform().getPosition().toInt().equals(event.getToTransform().getPosition().toInt())) {
            plotFacade.onPlayerMove(event.getFromTransform(), event.getToTransform(), (Player) event.getTargetEntity());
            townSpawnFacade.onPlayerMove(player);
        }
    }

    @Listener
    public void onPlayerSpawn(SpawnEntityEvent event) {
        if (config.SPAWN_IN_TOWN) {
            residentFacade.onPlayerSpawn(event);
        }
    }

    @Listener
    public void onPlayerLogin(ClientConnectionEvent.Join event, @Root Player player) {
        residentFacade.onLogin(player);
    }

    @Listener
    public void onPlayerDamage(DamageEntityEvent event, @Root EntityDamageSource source, @Getter("getTargetEntity") Player target) {
        EntityUtils.playerAttackedEntity(source).ifPresent(player -> {
            townFacade.onPlayerDamage(event, player);
        });
    }

    @Listener
    public void onPlayerVote(PlayerVoteEvent event) {
        pollFacade.onPlayerVote(event);
    }
}
