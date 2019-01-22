package com.atherys.towns.facade;

import com.atherys.towns.api.command.exception.TownsCommandException;
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

    TownFacade() {
    }

    public void createTown(Player player, Text name) throws CommandException {
        // get player's plot selection
        PlotSelection selection = plotSelectionFacade.getCurrentPlotSelection(player);

        // Validate the plot selection
        plotSelectionFacade.validatePlotSelection(selection);

        Plot homePlot = plotService.createPlotFromSelection(selection);

        if (plotService.plotIntersectsAnyOthers(homePlot)) {
            throw new TownsCommandException("The plot you've selected intersects with another.");
        }

        if (!plotService.isLocationWithinPlot(player.getLocation(), homePlot)) {
            throw new TownsCommandException("You must be within your plot selection in order to create a new town.");
        }

        Town town = townService.createTown(
                player.getWorld(),
                player.getTransform(),
                residentService.getOrCreate(player),
                homePlot,
                name
        );

        sendTownInfo(town, player);

        townsMsg.broadcastInfo(player.getName(), " has created the town of ", town.getName());
    }

    private void sendTownInfo(Town town, Player player) {
        // TODO
    }
}
