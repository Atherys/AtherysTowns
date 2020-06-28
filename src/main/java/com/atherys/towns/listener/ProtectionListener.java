package com.atherys.towns.listener;

import com.atherys.towns.facade.PlotFacade;
import com.google.inject.Inject;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.entity.InteractEntityEvent;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.item.ItemTypes;

import java.util.concurrent.atomic.AtomicBoolean;

public class ProtectionListener {

    @Inject
    PlotFacade plotFacade;

    @Listener
    public void onBlockInteract(InteractBlockEvent event, @Root Player player) {
        BlockType type = event.getTargetBlock().getState().getType();
        if(type != BlockTypes.AIR) {
            plotFacade.onBlockInteract(player, event);
        }
    }

    @Listener
    public void onEntityInteract(InteractEntityEvent event, @Root Player player) {
        if(!(event.getTargetEntity() instanceof Player)){
            if(event instanceof InteractEntityEvent.Primary.MainHand || event instanceof  InteractEntityEvent.Secondary.MainHand) {
                plotFacade.onEntityInteract(player, event);
            }
        }

    }

}
