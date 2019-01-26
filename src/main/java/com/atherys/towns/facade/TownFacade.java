package com.atherys.towns.facade;

import com.atherys.core.template.BookTemplate;
import com.atherys.core.template.TemplateEngine;
import com.atherys.towns.api.command.exception.TownsCommandException;
import com.atherys.towns.api.permission.town.TownPermissions;
import com.atherys.towns.entity.Plot;
import com.atherys.towns.entity.Town;
import com.atherys.towns.plot.PlotSelection;
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

            // Get the plot selection
            Plot homePlot = plotService.createPlotFromSelection(selection);

            // If it intersects any other plots, stop
            if (plotService.plotIntersectsAnyOthers(homePlot)) {
                throw new TownsCommandException("The plot you've selected intersects with another.");
            }

            // If the player is not within the plot selection, stop
            if (!plotService.isLocationWithinPlot(player.getLocation(), homePlot)) {
                throw new TownsCommandException("You must be within your plot selection in order to create a new town.");
            }

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
        Optional<Town> playerTown = residentFacade.getPlayerTown(player);

        if (!playerTown.isPresent()) {
            throw new TownsCommandException("You are not part of a town.");
        } else {
            sendTownInfo(playerTown.get(), player);
        }
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
            throw new TownsCommandException("Must be in a town to do this.");
        }

        return town;
    }

    private void sendTownInfo(Town town, Player player) {
        player.sendMessage(Text.of(town.toString()));

        // TODO
    }
}
