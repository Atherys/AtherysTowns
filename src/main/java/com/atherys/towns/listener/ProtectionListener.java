package com.atherys.towns.listener;

import com.atherys.towns.facade.PlotFacade;
import com.google.inject.Inject;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.entity.InteractEntityEvent;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent;

public class ProtectionListener {

    @Inject
    PlotFacade plotFacade;

    @Listener
    public void onBlockPlace(ChangeBlockEvent.Place event, @Root Player player) {
        plotFacade.plotAccessCheck(event, player, true);
    }

    @Listener
    public void onBlockBreak(ChangeBlockEvent.Break event, @Root Player player) {
        plotFacade.plotAccessCheck(event, player, true);
    }

    @Listener
    public void onBlockModify(ChangeBlockEvent.Modify event, @Root Player player) {
        plotFacade.plotAccessCheck(event, player, true);
    }

    @Listener
    public void onInventoryOpen(InteractInventoryEvent.Open event, @Root Player player) {
        plotFacade.plotAccessCheck(event, player, true);
    }

    @Listener
    public void onBlockInteract(InteractBlockEvent event, @Root Player player) {
        BlockType blockType = event.getTargetBlock().getState().getType();
        if (blockType == BlockTypes.TNT) {
            plotFacade.plotAccessCheck(event, player, true);
        }
    }

    @Listener
    public void onEntityInteract(InteractEntityEvent event, @Root Player player) {
        if (!(event.getTargetEntity() instanceof Player)) {
            if (event instanceof InteractEntityEvent.Primary.MainHand || event instanceof InteractEntityEvent.Secondary.MainHand) {
                plotFacade.plotAccessCheck(event, player, false);
            }
        }

    }

}
