package com.atherys.towns.listener;

import com.atherys.towns.api.permission.world.WorldPermissions;
import com.atherys.towns.facade.PlotFacade;
import com.google.inject.Inject;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.Hostile;
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
import org.spongepowered.api.event.filter.cause.Root;

public class ProtectionListener {

    @Inject
    PlotFacade plotFacade;

    public boolean isRedstone(BlockType blockType) {
        return blockType.getDefaultState().supports(Keys.POWERED) || blockType.getDefaultState().supports(Keys.POWER);
    }

    public boolean isTileEntity(BlockSnapshot snapshot) {
        return snapshot.getLocation().map(location -> location.getTileEntity().isPresent()).orElse(false);
    }

    public boolean isDoor(BlockType blockType) {
        return blockType.getDefaultState().supports(Keys.OPEN);
    }

    @Listener
    public void onBlockPlace(ChangeBlockEvent.Place event, @Root Player player) {
        event.getTransactions().forEach(blockSnapshotTransaction -> blockSnapshotTransaction.getOriginal().getLocation().ifPresent(location -> {
            // Block locations need to be shifted, so they don't appear to be on the edge of a town
            plotFacade.plotAccessCheck(event, player, WorldPermissions.BUILD,
                    location.copy().add(0.5, 0.5, 0.5), true);
        }));
    }

    @Listener
    public void onBlockBreak(ChangeBlockEvent.Break event, @Root Player player) {
        event.getTransactions().forEach(blockSnapshotTransaction -> blockSnapshotTransaction.getOriginal().getLocation().ifPresent(location -> {
            // Block locations need to be shifted, so they don't appear to be on the edge of a town
            plotFacade.plotAccessCheck(event, player, WorldPermissions.DESTROY,
                    location.copy().add(0.5, 0.5, 0.5), true);
        }));
    }

    @Listener
    public void onBlockChange(ChangeBlockEvent.Modify event, @Root Player player) {
        event.getTransactions().forEach(blockSnapshotTransaction -> blockSnapshotTransaction.getOriginal().getLocation().ifPresent(location -> {
            BlockType blockType = blockSnapshotTransaction.getOriginal().getState().getType();
            if (isDoor(blockType) || isRedstone(blockType) || isTileEntity(blockSnapshotTransaction.getOriginal())) return;
            // Block locations need to be shifted, so they don't appear to be on the edge of a town
            plotFacade.plotAccessCheck(event, player, WorldPermissions.BUILD,
                    location.copy().add(0.5, 0.5, 0.5), true);
        }));
    }

    @Listener
    public void onBlockInteract(InteractBlockEvent event, @Root Player player) {
        event.getTargetBlock().getLocation().ifPresent(location -> {
            BlockType blockType = event.getTargetBlock().getState().getType();
            if (isTileEntity(event.getTargetBlock())) {
                plotFacade.plotAccessCheck(event, player, WorldPermissions.INTERACT_TILE_ENTITIES,
                        location.copy().add(0.5, 0.5, 0.5), true);
            }
            if (isDoor(blockType)) {
                plotFacade.plotAccessCheck(event, player, WorldPermissions.INTERACT_DOORS,
                        location.copy().add(0.5, 0.5, 0.5), true);
            }
            if (isRedstone(blockType) && !isDoor(blockType)) {
                plotFacade.plotAccessCheck(event, player, WorldPermissions.INTERACT_REDSTONE,
                        location.copy().add(0.5, 0.5, 0.5), true);
            }
            if (blockType.equals(BlockTypes.TNT)) {
                plotFacade.plotAccessCheck(event, player, WorldPermissions.INTERACT_ENTITIES,
                        location.copy().add(0.5, 0.5, 0.5), true);
            }
        });
    }

    // @Listener
    // public void onEntitySpawn(SpawnEntityEvent event, @Root Player player) {
    //     if (event.getEntities().stream().noneMatch(entity -> entity instanceof Player)) {
    //         event.getEntities().forEach(entity -> {
    //             plotFacade.plotAccessCheck(event, player, WorldPermissions.SPAWN_ENTITIES, entity.getLocation(), true);
    //         });
    //     }
    // }

    @Listener
    public void onEntityInteract(InteractEntityEvent event, @Root Player player) {
        if (!(event.getTargetEntity() instanceof Player)) {
            //This prevents it from firing twice.
            if (event instanceof InteractEntityEvent.Primary.MainHand || event instanceof InteractEntityEvent.Secondary.MainHand) {
                plotFacade.plotAccessCheck(event, player, WorldPermissions.INTERACT_ENTITIES,
                        event.getTargetEntity().getLocation(), false);
            }
        }
    }

    @Listener
    public void onCollideBlock(CollideBlockEvent event, @Root Player player) {
        if (isRedstone(event.getTargetBlock().getType())) {
            plotFacade.plotAccessCheck(event, player, WorldPermissions.INTERACT_REDSTONE,
                    event.getTargetLocation(), false);
        }
    }

    @Listener
    public void onEntityDamage(CollideEntityEvent.Impact event) {
        event.getContext().get(EventContextKeys.OWNER).filter(user -> user.getPlayer().isPresent())
                .map(User::getPlayer).ifPresent(player -> {
            if (event.getEntities().stream().noneMatch(entity -> entity instanceof Player)) {
                event.getEntities().forEach(entity -> {
                    plotFacade.plotAccessCheck(event, player.get(), WorldPermissions.INTERACT_ENTITIES,
                            entity.getLocation(), true);
                });
            }
        });
    }

    @Listener
    public void onEntitySpawn(SpawnEntityEvent event) {
        event.filterEntities(entity -> {
            if (entity instanceof Hostile) {
                return plotFacade.checkHostileSpawningAtLocation(entity.getLocation());
            }

            return true;
        });
    }

}
