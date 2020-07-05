package com.atherys.towns.listener;

import com.atherys.towns.api.permission.world.WorldPermissions;
import com.atherys.towns.facade.PlotFacade;
import com.atherys.towns.facade.ProtectionFacade;
import com.google.inject.Inject;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.block.CollideBlockEvent;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.cause.EventContextKeys;
import org.spongepowered.api.event.entity.CollideEntityEvent;
import org.spongepowered.api.event.entity.InteractEntityEvent;
import org.spongepowered.api.event.entity.SpawnEntityEvent;
import org.spongepowered.api.event.entity.projectile.LaunchProjectileEvent;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.item.inventory.UseItemStackEvent;

public class ProtectionListener {

    @Inject
    PlotFacade plotFacade;

    @Inject
    ProtectionFacade protectionFacade;

    @Listener
    public void onBlockPlace(ChangeBlockEvent.Place event, @Root Player player) {
        event.getTransactions().forEach(blockSnapshotTransaction -> blockSnapshotTransaction.getOriginal().getLocation().ifPresent(location -> {
            plotFacade.plotAccessCheck(event, player, WorldPermissions.BUILD, location, true);
        }));

    }

    @Listener
    public void onBlockBreak(ChangeBlockEvent.Break event, @Root Player player) {
        event.getTransactions().forEach(blockSnapshotTransaction -> blockSnapshotTransaction.getOriginal().getLocation().ifPresent(location -> {
            plotFacade.plotAccessCheck(event, player, WorldPermissions.DESTROY, location, true);
        }));
    }

    @Listener
    public void onBlockChange(ChangeBlockEvent.Modify event, @Root Player player) {
        event.getTransactions().forEach(blockSnapshotTransaction -> blockSnapshotTransaction.getOriginal().getLocation().ifPresent(location -> {
            plotFacade.plotAccessCheck(event, player, WorldPermissions.BUILD, location, true);
        }));
    }

    @Listener
    public void onProjectileLaunch(LaunchProjectileEvent event, @Root Player player) {
        plotFacade.plotAccessCheck(event, player, WorldPermissions.USE_ITEMS, player.getLocation(), true);
    }

    @Listener
    public void onBlockInteract(InteractBlockEvent event, @Root Player player) {
        event.getTargetBlock().getLocation().ifPresent(location -> {
            BlockType blockType = event.getTargetBlock().getState().getType();
            if (protectionFacade.isTileEntity(event)) {
                plotFacade.plotAccessCheck(event, player, WorldPermissions.INTERACT_TILE_ENTITIES, location, true);
            }
            if (protectionFacade.isDoor(blockType)) {
                plotFacade.plotAccessCheck(event, player, WorldPermissions.INTERACT_DOORS, location, true);
            }
            if (protectionFacade.isRedstone(blockType) && !protectionFacade.isDoor(blockType)) {
                plotFacade.plotAccessCheck(event, player, WorldPermissions.INTERACT_REDSTONE, location, true);
            }
            if (blockType.getTrait("EXPLODE").isPresent()) {
                plotFacade.plotAccessCheck(event, player, WorldPermissions.USE_ITEMS, location, true);
            }
        });
    }

    @Listener
    public void onEntitySpawn(SpawnEntityEvent event, @Root Player player) {
        if (event.getEntities().stream().noneMatch(entity -> entity instanceof Player)) {
            event.getEntities().forEach(entity -> {
                plotFacade.plotAccessCheck(event, player, WorldPermissions.BUILD, entity.getLocation(), true);
            });
        }
    }

    @Listener
    public void onEntityInteract(InteractEntityEvent event, @Root Player player) {
        if (!(event.getTargetEntity() instanceof Player)) {
            //This prevents it from firing twice.
            if (event instanceof InteractEntityEvent.Primary.MainHand || event instanceof InteractEntityEvent.Secondary.MainHand) {
                plotFacade.plotAccessCheck(event, player, WorldPermissions.INTERACT_ENTITIES, event.getTargetEntity().getLocation(), false);
            }
        }
    }

    @Listener
    public void onItemUse(UseItemStackEvent.Start event, @Root Player player) {
        if (!protectionFacade.isExemptItem(event.getItemStackInUse())) {
            plotFacade.plotAccessCheck(event, player, WorldPermissions.USE_ITEMS, player.getLocation(), true);
        }
    }

    @Listener
    public void onCollideBlock(CollideBlockEvent event, @Root Player player) {
        if (protectionFacade.isRedstone(event.getTargetBlock().getType())) {
            plotFacade.plotAccessCheck(event, player, WorldPermissions.INTERACT_REDSTONE, event.getTargetLocation(), false);
        }
    }

    @Listener
    public void onEntityDamage(CollideEntityEvent.Impact event) {
        event.getContext().get(EventContextKeys.OWNER).filter(user -> user.getPlayer().isPresent())
                .map(User::getPlayer).ifPresent(player -> {
            if (event.getEntities().stream().noneMatch(entity -> entity instanceof Player)) {
                event.getEntities().forEach(entity -> {
                    plotFacade.plotAccessCheck(event, player.get(), WorldPermissions.USE_ITEMS, entity.getLocation(), true);
                });
            }
        });
    }

    @Listener
    public void onCollideBlock(CollideBlockEvent event) {
        event.getContext().get(EventContextKeys.OWNER).filter(user -> user.getPlayer().isPresent())
                .map(User::getPlayer).ifPresent(player -> {
            if (protectionFacade.isRedstone(event.getTargetBlock().getType())) {
                plotFacade.plotAccessCheck(event, player.get(), WorldPermissions.INTERACT_REDSTONE, event.getTargetLocation(), false);
            } else if (!(event.getSource() instanceof Player)) {
                plotFacade.plotAccessCheck(event, player.get(), WorldPermissions.DAMAGE_NONPLAYERS, event.getTargetLocation(), true);
            }
        });
    }

}
