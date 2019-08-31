package com.atherys.towns.facade;

import com.atherys.towns.api.command.exception.TownsCommandException;
import com.atherys.towns.api.permission.town.TownPermissions;
import com.atherys.towns.entity.Plot;
import com.atherys.towns.service.PlotService;
import com.atherys.towns.service.ResidentService;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.title.Title;
import org.spongepowered.api.world.World;

import java.util.Optional;

@Singleton
public class PlotFacade {

    @Inject
    PlotService plotService;

    @Inject
    PermissionFacade permissionFacade;

    @Inject
    TownsMessagingFacade townsMsg;

    @Inject
    ResidentService residentService;

    PlotFacade() {
    }

    public void renamePlotAtPlayerLocation(Player player, Text newName) throws TownsCommandException {
        Plot plot = plotService.getPlotByLocation(player.getLocation()).orElseThrow(() -> {
            return new TownsCommandException("No plot found at your position");
        });

        if (permissionFacade.isPermitted(player, plot.getTown(), TownPermissions.RENAME_PLOT) ||
            residentService.getOrCreate(player).equals(plot.getOwner())) {

            plotService.setPlotName(plot, newName);
            townsMsg.info(player, "Plot renamed.");
        } else {
            throw new TownsCommandException("You are not permitted to rename this plot.");
        }
    }

    public void sendInfoOnPlotAtPlayerLocation(Player player) throws TownsCommandException {
        Plot plot = plotService.getPlotByLocation(player.getLocation()).orElseThrow(() -> {
            return new TownsCommandException("No plot found at your position.");
        });

        Text message = Text.builder()
                .append(Text.of("Plot: ", plot.getName()))
                .append(Text.of("Size: ", plotService.getPlotArea(plot)))
                .append(Text.of("Point A: ", plot.getNorthEastCorner()))
                .append(Text.of("Point B: ", plot.getSouthWestCorner()))
                .append(Text.of("Town: ", plot.getTown().getName()))
                .build();

        player.sendMessage(message);
    }



    public void onPlayerMove(Transform<World> from, Transform<World> to, Player player) {
        Optional<Plot> plot = plotService.getPlotByLocation(to.getLocation());
        if (!plot.isPresent()) return;

        Optional<Plot> plotFrom = plotService.getPlotByLocation(from.getLocation());
        if (plotFrom.isPresent()) return;

        player.sendTitle(Title.builder().stay(20).title(plot.get().getTown().getName()).build());
    }
}
