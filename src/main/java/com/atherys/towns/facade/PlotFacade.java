package com.atherys.towns.facade;

import com.atherys.towns.api.command.TownsCommandException;
import com.atherys.towns.api.permission.town.TownPermissions;
import com.atherys.towns.model.entity.Plot;
import com.atherys.towns.service.PlotService;
import com.atherys.towns.service.ResidentService;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.title.Title;
import org.spongepowered.api.world.World;

import java.util.Optional;

import static org.spongepowered.api.text.format.TextColors.DARK_GREEN;
import static org.spongepowered.api.text.format.TextColors.GOLD;

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
        Plot plot = getPlotAtPlayer(player);

        if (permissionFacade.isPermitted(player, TownPermissions.RENAME_PLOT) ||
            residentService.getOrCreate(player).equals(plot.getOwner())) {

            plotService.setPlotName(plot, newName);
            townsMsg.info(player, "Plot renamed.");
        } else {
            throw new TownsCommandException("You are not permitted to rename this plot.");
        }
    }

    public void sendInfoOnPlotAtPlayerLocation(Player player) throws TownsCommandException {
        Plot plot = getPlotAtPlayer(player);

        Text message = Text.builder()
                .append(Text.of("Plot: ", plot.getName(), Text.NEW_LINE))
                .append(Text.of("Size: ", plotService.getPlotArea(plot), Text.NEW_LINE))
                .append(Text.of("Point A: ", plot.getNorthEastCorner(), Text.NEW_LINE))
                .append(Text.of("Point B: ", plot.getSouthWestCorner(), Text.NEW_LINE))
                .append(Text.of("Town: ", plot.getTown().getName(), Text.NEW_LINE))
                .append(Text.of("Owner: ", plot.getOwner() == null ? "None" : plot.getOwner().getName()))
                .build();

        player.sendMessage(message);
    }

    public void grantPlayerPlotAtPlayerLocation(Player player, User target) throws TownsCommandException {
        Plot plot = getPlotAtPlayer(player);

        plotService.setPlotOwner(plot, residentService.getOrCreate(target));
        townsMsg.info(player, "Granted the plot ", GOLD, plot.getName(), DARK_GREEN, " to ", GOLD, target.getName(), DARK_GREEN, ".");
    }

    private Plot getPlotAtPlayer(Player player) throws TownsCommandException {
        return plotService.getPlotByLocation(player.getLocation()).orElseThrow(() -> {
            return new TownsCommandException("No plot found at your position");
        });
    }

    public void onPlayerMove(Transform<World> from, Transform<World> to, Player player) {
        Optional<Plot> plot = plotService.getPlotByLocation(to.getLocation());
        if (!plot.isPresent()) return;

        Optional<Plot> plotFrom = plotService.getPlotByLocation(from.getLocation());
        if (plotFrom.isPresent()) return;

        player.sendTitle(Title.builder().stay(20).title(Text.of(plot.get().getTown().getName())).build());
    }
}
