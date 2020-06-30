package com.atherys.towns.listener;

import com.atherys.towns.api.permission.world.WorldPermissions;
import com.atherys.towns.facade.PlotFacade;
import com.atherys.towns.facade.ProtectionFacade;
import com.google.inject.Inject;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.entity.InteractEntityEvent;
import org.spongepowered.api.event.entity.projectile.LaunchProjectileEvent;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.item.inventory.InteractItemEvent;

public class ProtectionListener {

    @Inject
    PlotFacade plotFacade;

    @Inject
    ProtectionFacade protectionFacade;

    @Listener
    public void onBlockPlace(ChangeBlockEvent.Place event, @Root Player player) {
        plotFacade.plotAccessCheck(event, player, WorldPermissions.BUILD, true);
    }

    @Listener
    public void onBlockBreak(ChangeBlockEvent.Break event, @Root Player player) {
        plotFacade.plotAccessCheck(event, player, WorldPermissions.DESTROY, true);
    }

    @Listener
    public void onPlayerThrow(LaunchProjectileEvent event, @Root Player player) {
        plotFacade.plotAccessCheck(event, player, WorldPermissions.USE_ITEMS, true);
    }

    @Listener
    public void onBlockInteract(InteractBlockEvent event, @Root Player player) {
        BlockType blockType = event.getTargetBlock().getState().getType();
        if (protectionFacade.isChest(blockType)) {
            plotFacade.plotAccessCheck(event, player, WorldPermissions.INTERACT_CHESTS, true);
        }
        if (protectionFacade.isDoor(blockType)) {
            plotFacade.plotAccessCheck(event, player, WorldPermissions.INTERACT_DOORS, true);
        }
        if (protectionFacade.isRedstone(blockType)) {
            plotFacade.plotAccessCheck(event, player, WorldPermissions.INTERACT_REDSTONE, true);
        }
    }

    @Listener
    public void onInteractItem(InteractItemEvent event, @Root Player player) {
        if (protectionFacade.isUseItem(player)) {
            plotFacade.plotAccessCheck(event, player, WorldPermissions.USE_ITEMS, true);
        }
    }

    @Listener
    public void onEntityInteract(InteractEntityEvent event, @Root Player player) {
        if (!(event.getTargetEntity() instanceof Player)) {
            if (event instanceof InteractEntityEvent.Primary.MainHand || event instanceof InteractEntityEvent.Secondary.MainHand) {
                plotFacade.plotAccessCheck(event, player, WorldPermissions.INTERACT_ENTITIES, false);
            }
        }

    }

}
