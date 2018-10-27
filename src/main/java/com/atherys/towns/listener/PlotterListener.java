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
        //This Event shall be called every time an user crosses a claim/plot.
        //This event then has to refresh internal cache on resident.
        //The cache contains basic boolean flags for action such as pvp, pve, block destroy, block interact


        /*
        class PermissionCache {
            bool mayAttackPlayers;
            bool mayAttackMobs;
            bool mayBuild;
            bool mayDestroy;
            ...
        }

        Some of these flags will partially reflect permission nodes found in ResidentRights.
        >Player changes plot (eg steps from a town to a wilderness/notclaimedarea)
        >In the town player was not permitted to build, in wilderness he can
        >you set the maybuild flag to true
        >During the BlockChangeEvent you validate only whenever maybuild flat is set to true
        No matter how luckpemrs is fast this will be always faster with much smaller overhead.
         */

    }
}
