package com.atherys.towns.listener;

import com.atherys.towns.AtherysTowns;
import com.atherys.towns.model.Plot;
import com.atherys.towns.model.Resident;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.filter.IsCancelled;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.util.Tristate;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;
import java.util.UUID;

public class PlayerActionListener {

    //Async event handler
    @IsCancelled(Tristate.FALSE)
    @Listener(order = Order.LAST)
    public void clientConnectedEvent(ClientConnectionEvent.Auth event) {
        UUID uniqueId = event.getProfile().getUniqueId();
        Resident resident = AtherysTowns.getResidentManager().getOrCreate(uniqueId);
        AtherysTowns.getPermissionManager().resolveResidentPermissions(resident);
    }


    @Listener(order = Order.LATE)
    @IsCancelled(Tristate.FALSE)
    public void onPlayerMove(MoveEntityEvent event, @Root Player player) {
        Location<World> locationFrom = event.getToTransform().getLocation();
        Location<World> locationTo = event.getFromTransform().getLocation();
        int blockX = locationFrom.getBlockX();
        int blockZ = locationFrom.getBlockZ();
        int blockX1 = locationTo.getBlockX();
        int blockZ1 = locationTo.getBlockZ();
        if (blockX == blockX1 && blockZ == blockZ1) {
            return;
        }
        Optional<Plot> plotByLocation = AtherysTowns.getPlotManager().getPlotByLocation(locationTo);
        Resident resident = AtherysTowns.getResidentManager().getOrCreate(player.getUniqueId());


        AtherysTowns.getPermissionManager().updatePermsCache(plotByLocation.orElse(null), resident);
        //display message to a player that the plot was changed?
    }

    @Listener(order = Order.LATE)
    @IsCancelled(Tristate.FALSE)
    public void onPlayerBreakBlock(ChangeBlockEvent.Break event, @Root Player player) {
        Transaction<BlockSnapshot> blockSnapshotTransaction = event.getTransactions().get(0);
        Optional<Location<World>> location = blockSnapshotTransaction.getOriginal().getLocation();
        if (!location.isPresent()) {
            throw new RuntimeException("Holy fuck what just happened?");
        }

        Optional<Plot> plotByLocation = AtherysTowns.getPlotManager().getPlotByLocation(location.get());
        Resident resident = AtherysTowns.getResidentManager().getOrCreate(player.getUniqueId());
        AtherysTowns.getPermissionManager().updatePermsCache(plotByLocation.orElse(null), resident);
        event.setCancelled(!resident.getPermsCache().mayDestroyBlocks);
    }

    @Listener(order = Order.LATE)
    @IsCancelled(Tristate.FALSE)
    public void onPlayerBuildBlock(ChangeBlockEvent.Place event, @Root Player player) {
        Transaction<BlockSnapshot> blockSnapshotTransaction = event.getTransactions().get(0);
        Optional<Location<World>> location = blockSnapshotTransaction.getOriginal().getLocation();
        if (!location.isPresent()) {
            throw new RuntimeException("Holy fuck what just happened?");
        }

        Optional<Plot> plotByLocation = AtherysTowns.getPlotManager().getPlotByLocation(location.get());
        Resident resident = AtherysTowns.getResidentManager().getOrCreate(player.getUniqueId());
        AtherysTowns.getPermissionManager().updatePermsCache(plotByLocation.orElse(null), resident);
        event.setCancelled(!resident.getPermsCache().mayBuildBlocks);
    }

    @Listener(order = Order.LATE)
    @IsCancelled(Tristate.FALSE)
    public void onEntityDamage(DamageEntityEvent event, @First(typeFilter = EntityDamageSource.class) EntityDamageSource entityDamageSource) {
        Entity targetEntity = event.getTargetEntity();
        Entity source = entityDamageSource.getSource();
        if (source.getType() == EntityTypes.PLAYER) {
            Resident resident = AtherysTowns.getResidentManager().getOrCreate(source.getUniqueId());
            Location<World> sourceLoc = source.getLocation();

            //Unfortunetly we have to check this every time player attacks an entity.
            //Player may attack someone else from another chunk
            Optional<Plot> plotByLocation = AtherysTowns.getPlotManager().getPlotByLocation(sourceLoc);
            AtherysTowns.getPermissionManager().updatePermsCache(plotByLocation.orElse(null), resident);

            Location<World> targetLoc = targetEntity.getLocation();
            if (targetEntity.getType() == EntityTypes.PLAYER)  {
                Resident targetResident = AtherysTowns.getResidentManager().getOrCreate(source.getUniqueId());
                Optional<Plot> targetResidentPlot = AtherysTowns.getPlotManager().getPlotByLocation(targetLoc);
                AtherysTowns.getPermissionManager().updatePermsCache(targetResidentPlot.orElse(null), targetResident);
                event.setCancelled(!(resident.getPermsCache().mayDamagePlayers && targetResident.getPermsCache().mayDamagePlayers));
            } else {
                event.setCancelled(!resident.getPermsCache().mayDamageEntities);
            }
        } else {
            //todo handle when is possible to be damaged or damage (passive)mobs
        }

    }
}
