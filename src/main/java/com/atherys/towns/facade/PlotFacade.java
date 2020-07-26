package com.atherys.towns.facade;

import com.atherys.towns.api.command.TownsCommandException;
import com.atherys.towns.api.permission.town.TownPermissions;
import com.atherys.towns.api.permission.world.WorldPermission;
import com.atherys.towns.model.entity.TownPlot;
import com.atherys.towns.model.entity.Resident;
import com.atherys.towns.service.PlotService;
import com.atherys.towns.service.ResidentService;
import com.atherys.towns.util.MathUtils;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.title.Title;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;

import static org.spongepowered.api.text.format.TextColors.*;

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

    PlotFacade() {}

    public void renameTownPlotAtPlayerLocation(Player player, Text newName) throws TownsCommandException {
        TownPlot plot = getPlotAtPlayer(player);

        if (permissionFacade.isPermitted(player, TownPermissions.RENAME_PLOT) ||
                residentService.getOrCreate(player).equals(plot.getOwner())) {

            plotService.setTownPlotName(plot, newName);
            townsMsg.info(player, "Plot renamed.");
        } else {
            throw new TownsCommandException("You are not permitted to rename this plot.");
        }
    }

    public void sendInfoOnPlotAtPlayerLocation(Player player) throws TownsCommandException {
        TownPlot plot = getPlotAtPlayer(player);
        Resident plotOwner = (plot.getOwner() != null) ? plot.getOwner() : new Resident();
        String ownerName = (plotOwner.getName() != null) ? plotOwner.getName() : "None";
        Text.Builder plotText = Text.builder();

        plotText.append(townsMsg.createTownsHeader(plot.getName().toPlain()));

        plotText.append(Text.of(
                DARK_GREEN, "Town: ",
                plot.getTown() == null ? Text.of(RED, "None") : Text.of(GOLD, plot.getTown().getName()),
                Text.NEW_LINE
        ));

        plotText
                .append(Text.of(DARK_GREEN, "Owner: ", GOLD, ownerName, Text.NEW_LINE))
                .append(Text.of(DARK_GREEN, "Size: ", GOLD, MathUtils.getArea(plot), Text.NEW_LINE))
                .append(Text.of(DARK_GREEN, "Point A: ", GOLD, "x: ", plot.getSouthWestCorner().getX(), ", z: ", plot.getSouthWestCorner().getY(), Text.NEW_LINE))
                .append(Text.of(DARK_GREEN, "Point B: ", GOLD, "x: ", plot.getNorthEastCorner().getX(), ", z: ", plot.getNorthEastCorner().getY()));

        player.sendMessage(plotText.build());
    }

    public void grantPlayerPlotAtPlayerLocation(Player player, User target) throws TownsCommandException {
        TownPlot plot = getPlotAtPlayer(player);

        plotService.setTownPlotOwner(plot, residentService.getOrCreate(target));
        townsMsg.info(player, "Granted the plot ", GOLD, plot.getName(), DARK_GREEN, " to ", GOLD, target.getName(), DARK_GREEN, ".");
    }

    private TownPlot getPlotAtPlayer(Player player) throws TownsCommandException {
        return plotService.getTownPlotByLocation(player.getLocation()).orElseThrow(() ->
                new TownsCommandException("No plot found at your position"));
    }

    private Optional<TownPlot> getPlotAtPlayerOptional(Player player) {
        return plotService.getTownPlotByLocation(player.getLocation());
    }

    public boolean hasPlotAccess(Player player, TownPlot plot, WorldPermission permission) {
        Resident resPlayer = residentService.getOrCreate(player);

        if (plot.getOwner() != null) {
            Resident plotOwner = plot.getOwner();
            if ((plotOwner == resPlayer) || plotOwner.getFriends().contains(resPlayer)) {
                return true;
            }
        }
        return player.hasPermission(permission.getId());
    }

    public void plotAccessCheck(Cancellable event, Player player, WorldPermission permission, Location<World> location, boolean messageUser) {
        plotService.getTownPlotByLocation(location).ifPresent(plot -> {
            if (!hasPlotAccess(player, plot, permission)) {
                if (messageUser) {
                    townsMsg.error(player, "You do not have permission to do that!");
                }
                event.setCancelled(true);
            }
        });
    }

    public void onPlayerMove(Transform<World> from, Transform<World> to, Player player) {
        Optional<TownPlot> plotTo = plotService.getTownPlotByLocation(to.getLocation());
        Optional<TownPlot> plotFrom = plotService.getTownPlotByLocation(from.getLocation());

        if (!plotTo.isPresent()) return;
        if (plotFrom.isPresent()) return;

        player.sendTitle(Title.builder().stay(20).title(Text.of(plotTo.get().getTown().getName())).build());
    }

}
