package com.atherys.towns.listener;

import com.atherys.towns.AtherysTowns;
import com.atherys.towns.events.PlotBorderCrossEvent;
import com.flowpowered.math.vector.Vector2d;
import com.flowpowered.math.vector.Vector3i;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
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

    @Listener
    public void onBorderCross(PlotBorderCrossEvent event, @Root Living living) {

    }
}
