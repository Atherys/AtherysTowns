package com.atherys.towns.facade;

import com.atherys.towns.api.command.exception.TownsCommandException;
import com.atherys.towns.entity.Plot;
import com.atherys.towns.service.PlotService;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.Optional;

@Singleton
public class PlotFacade {

    @Inject
    PlotService plotService;

    PlotFacade() {
    }

    public void renamePlotAtPlayerLocation(Player player, Text newName) throws CommandException {
        Optional<Plot> plot = plotService.getPlotByLocation(player.getLocation());

        if (!plot.isPresent()) {
            throw new TownsCommandException("Could not find a plot to rename at present location");
        } else {
            plotService.setPlotName(plot.get(), newName);
        }
    }

    public void sendInfoOnPlotAtPlayerLocation(Player player) {
        // TODO
    }
}
