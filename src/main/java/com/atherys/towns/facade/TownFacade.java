package com.atherys.towns.facade;

import com.atherys.towns.api.command.exception.TownsCommandException;
import com.atherys.towns.api.permission.town.TownPermissions;
import com.atherys.towns.entity.Plot;
import com.atherys.towns.entity.Resident;
import com.atherys.towns.entity.Town;
import com.atherys.towns.plot.PlotSelection;
import com.atherys.towns.service.PermissionService;
import com.atherys.towns.service.PlotService;
import com.atherys.towns.service.ResidentService;
import com.atherys.towns.service.TownService;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.Optional;

@Singleton
public class TownFacade {

    @Inject
    PlotSelectionFacade plotSelectionFacade;

    @Inject
    PlotService plotService;

    @Inject
    TownService townService;

    @Inject
    ResidentService residentService;

    @Inject
    TownsMessagingFacade townsMsg;

    @Inject
    ResidentFacade residentFacade;

    @Inject
    PermissionFacade permissionFacade;

    @Inject
    PermissionService permissionService;

    TownFacade() {
    }

    public void createTown(Player player, String name) throws CommandException {
        if (name == null || name.isEmpty()) {
            throw new TownsCommandException("Must provide a town name.");
        }

        // get player's plot selection
        PlotSelection selection = plotSelectionFacade.getCurrentPlotSelection(player);

        // Validate the plot selection
        if (plotSelectionFacade.validatePlotSelection(selection)) {

            // Create a plot from the selection
            Plot homePlot = plotService.createPlotFromSelection(selection);

            // If it intersects any other plots, stop
            if (plotService.plotIntersectsAnyOthers(homePlot)) {
                throw new TownsCommandException("The plot you've selected intersects with another.");
            }

            // If the player is not within the plot selection, stop
            if (!plotService.isLocationWithinPlot(player.getLocation(), homePlot)) {
                throw new TownsCommandException("You must be within your plot selection in order to create a new town.");
            }

            // create the town
            Town town = townService.createTown(
                    player.getWorld(),
                    player.getTransform(),
                    residentService.getOrCreate(player),
                    homePlot,
                    Text.of(name)
            );

            sendTownInfo(town, player);

            townsMsg.broadcastInfo(player.getName(), " has created the town of ", town.getName());
        }
    }

    public void sendTownInfo(Player player) throws TownsCommandException {
        sendTownInfo(getPlayerTown(player), player);
    }

    public void sendTownInfo(Player player, String townName) throws TownsCommandException {
        if (townName == null || townName.isEmpty()) {
            throw new TownsCommandException("Empty town name.");
        }

        Optional<Town> town = townService.getTownFromName(townName);

        if (!town.isPresent()) {
            throw new TownsCommandException("No town called \"", townName, "\" could be found.");
        }

        sendTownInfo(town.get(), player);
    }

    public void setPlayerTownName(Player source, String name) throws TownsCommandException {
        if (name == null || name.isEmpty()) {
            throw new TownsCommandException("Must provide a new town name.");
        }

        Town town = getPlayerTown(source);

        if (permissionFacade.isPermitted(source, town, TownPermissions.SET_NAME)) {
            townService.setTownName(town, name);
        }
    }

    public Town getPlayerTown(Player source) throws TownsCommandException {
        Town town = residentService.getOrCreate(source).getTown();

        if (town == null) {
            throw TownsCommandException.notPartOfTown();
        }

        return town;
    }

    public void abandonTownPlotAtPlayerLocation(Player source) throws TownsCommandException {
        Optional<Plot> plot = plotService.getPlotByLocation(source.getLocation());

        if (!plot.isPresent()) {
            throw new TownsCommandException("You are not currently standing on a claim area.");
        }

        Resident resident = residentService.getOrCreate(source);
        Town town = resident.getTown();

        if (town == null) {
            throw TownsCommandException.notPartOfTown();
        }

        if (permissionFacade.isPermitted(source, town, TownPermissions.UNCLAIM_PLOT)) {
            townService.removePlotFromTown(town, plot.get());
            townsMsg.info(source, "Plot abandoned.");
        }
    }

    public void claimTownPlotFromPlayerSelection(Player source) throws CommandException {
        PlotSelection selection = plotSelectionFacade.getValidPlayerPlotSelection(source);

        Resident resident = residentService.getOrCreate(source);
        Town town = resident.getTown();

        if (town == null) {
            throw TownsCommandException.notPartOfTown();
        }

        if (permissionFacade.isPermitted(source, town, TownPermissions.CLAIM_PLOT)) {
            Plot plot = plotService.createPlotFromSelection(selection);

            if (!plotService.plotBordersTown(town, plot)) {
                throw new TownsCommandException("New plot does not border the town it's being claimed for.");
            }

            if (plotService.plotIntersectsAnyOthers(plot)) {
                throw new TownsCommandException("The plot selection intersects with an already-existing plot.");
            }

            townService.claimPlotForTown(plot, town);

            townsMsg.info(source, "Plot claimed.");
        }
    }

    private void sendTownInfo(Town town, Player player) {
        player.sendMessage(Text.of(town.toString()));

        // TODO
    }
}
