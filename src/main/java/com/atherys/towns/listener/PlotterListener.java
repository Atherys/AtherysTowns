package com.atherys.towns.listener;

import com.atherys.towns.AtherysTowns;
import com.atherys.towns.events.PlotBorderCrossEvent;
import com.atherys.towns.model.Plot;
import com.atherys.towns.model.Resident;
import com.atherys.towns.persistence.PermissionManager;
import com.atherys.towns.persistence.ResidentManager;
import com.flowpowered.math.vector.Vector2d;
import com.flowpowered.math.vector.Vector3i;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.filter.cause.Root;

public class PlotterListener {
    @Listener
    public void onClick(InteractBlockEvent.Secondary event, @Root Player player) {
        if (AtherysTowns.getPlottingService().isPlotting(player)) {
            Vector3i point = event.getTargetBlock().getPosition();
            AtherysTowns.getPlottingService().progress(player, Vector2d.from(point.getX(), point.getZ()));
        }
    }

    @Listener(order = Order.LATE)
    public void onBorderCross(PlotBorderCrossEvent event, @Root Player living) {
        Resident resident = AtherysTowns.getResidentManager().getOrCreate(living.getUniqueId());
        if (event.isCancelled()) {
            event.getMoveEvent().setCancelled(true);
        } else {
            Plot to = event.getTo();
            Plot from = event.getFrom();
        }
    }
}
