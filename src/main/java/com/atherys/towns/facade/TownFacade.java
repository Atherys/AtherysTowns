package com.atherys.towns.facade;

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

        Town town = townService.createTown(
                residentService.getOrCreate(player),
                plotService.createPlotFromSelection(selection),
                name
        );

        townsMsg.info(player, "Your town has been created successfully!");

        sendTownInfo(town, player);

        townsMsg.broadcastInfo(player.getName(), " has created the town of ", town.getName());
    }

    private void sendTownInfo(Town town, Player player) {
        // TODO
    }
}
