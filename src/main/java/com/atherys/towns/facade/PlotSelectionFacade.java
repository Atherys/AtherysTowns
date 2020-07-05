package com.atherys.towns.facade;

import com.atherys.towns.TownsConfig;
import com.atherys.towns.model.entity.Plot;
import com.atherys.towns.plot.PlotSelection;
import com.atherys.towns.service.PlotService;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Singleton
public class PlotSelectionFacade {

    private final Map<UUID, PlotSelection> selections = new HashMap<>();
    @Inject
    TownsConfig config;
    @Inject
    TownsMessagingFacade townMsg;
    @Inject
    PlotService plotService;
    @Inject
    PlotBorderFacade plotBorderFacade;

    private PlotSelection getOrCreateSelection(Player player) {
        if (selections.containsKey(player.getUniqueId())) {
            return selections.get(player.getUniqueId());
        } else {
            PlotSelection plotSelection = new PlotSelection();
            selections.put(player.getUniqueId(), plotSelection);
            return plotSelection;
        }
    }

    public void clearSelection(Player player) {
        selections.remove(player.getUniqueId());
        plotBorderFacade.refreshBorders(player, player.getLocation());
        townMsg.info(player, "You have cleared your selection.");
    }

    /**
     * Calculate the area of a plot selection.
     *
     * @param plotSelection The selection whose area is to be calculated
     * @return The plot selection area, or -1 if the plot selection is invalid ( null or incomplete )
     */
    private int getPlotSelectionArea(PlotSelection plotSelection) {
        if (plotSelection == null || !plotSelection.isComplete()) {
            return -1;
        }

        return Math.abs(plotSelection.getPointA().getBlockX() - plotSelection.getPointB().getBlockX()) *
                Math.abs(plotSelection.getPointA().getBlockZ() - plotSelection.getPointB().getBlockZ());
    }

    private int getSmallestPlotSelectionSideSize(PlotSelection plotSelection) {
        if (plotSelection == null || !plotSelection.isComplete()) {
            return -1;
        }

        int sideX = Math.abs(plotSelection.getPointA().getBlockX() - plotSelection.getPointB().getBlockX());
        int sideZ = Math.abs(plotSelection.getPointA().getBlockZ() - plotSelection.getPointB().getBlockZ());
        return Math.min(sideX, sideZ);
    }

    public void checkBorders(Player player) {
        PlotSelection selection = getOrCreateSelection(player);
        if (selection.isComplete()) {
            validatePlotSelection(selection, player, true, player.getLocation());
            plotBorderFacade.clearBorders(player);
            plotBorderFacade.showNewPlotSelectionBorders(player, player.getLocation());
        }
    }

    private void selectPointAAtLocation(Player player, Location<World> location) {
        getOrCreateSelection(player).setPointA(location);
        checkBorders(player);
    }

    private void selectPointBAtLocation(Player player, Location<World> location) {
        getOrCreateSelection(player).setPointB(location);
        checkBorders(player);
    }

    public void selectPointAFromPlayerLocation(Player player) {
        selectPointAAtLocation(player, player.getLocation());
        sendPointSelectionMessage(player, "A");
    }

    public void selectPointBFromPlayerLocation(Player player) {
        selectPointBAtLocation(player, player.getLocation());
        sendPointSelectionMessage(player, "B");
    }

    private void sendPointSelectionMessage(Player player, String point) {
        Location<World> location = player.getLocation();
        townMsg.info(player, "You have selected point", TextColors.GOLD, " ", point, " ", TextColors.DARK_GREEN,
                "at ", location.getBlockX(), ", ", location.getBlockY(), ", ", location.getBlockZ(), ".");
    }

    /**
     * Validate a plot selection.
     *
     * @param selection the selection to be validated
     * @throws CommandException if the plot selection is null, is incomplete ( either point A or point B is null ),
     *                          it's area is greater than the maximum configured, or it's smallest side is smaller than the minimum configured
     */
    public boolean validatePlotSelection(PlotSelection selection, Player player, boolean messageUser, Location<World> location) {

        if (selection == null) {
            if (messageUser) townMsg.error(player, "Plot selection is null.");
            return false;
        }

        if (!selection.isComplete()) {
            if (messageUser) townMsg.error(player, "Plot selection is incomplete.");
            return false;
        }

        int selectionArea = getPlotSelectionArea(selection);
        if (selectionArea > config.TOWN.MAX_PLOT_AREA) {
            if (messageUser)
                townMsg.error(player, "Plot selection has an area greater than permitted ( ", selectionArea, " > ", config.TOWN.MAX_PLOT_AREA, " )");
            return false;
        }

        int smallestSide = getSmallestPlotSelectionSideSize(selection);
        if (smallestSide < config.TOWN.MIN_PLOT_SIDE - 1) {
            if (messageUser)
                townMsg.error(player, "Plot selection has a side smaller than permitted ( ", smallestSide, " < ", config.TOWN.MIN_PLOT_SIDE, " )");
            return false;
        }

        Plot plot = plotService.createPlotFromSelection(selection);
        if (plotService.plotIntersectsAnyOthers(plot)) {
            if (messageUser) townMsg.error(player, "The plot selection intersects with an already-existing plot.");
            return false;
        }

        if (!plotService.isLocationWithinPlot(location, plot)) {
            if (messageUser) townMsg.error(player, "You must be within your plot selection!");
            return false;
        }
        return true;
    }

    public PlotSelection getCurrentPlotSelection(Player player) {
        return getOrCreateSelection(player);
    }
}
