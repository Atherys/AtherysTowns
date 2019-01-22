package com.atherys.towns.facade;

import com.atherys.towns.TownsConfig;
import com.atherys.towns.api.command.exception.TownsCommandException;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Singleton
public class PlotSelectionFacade {

    @Inject
    TownsConfig config;

    public static class PlotSelection {

        private Location<World> pointA;

        private Location<World> pointB;

        PlotSelection() {
        }

        public void setPointA(Location<World> pointA) {
            this.pointA = pointA;
        }

        public void setPointB(Location<World> pointB) {
            this.pointB = pointB;
        }

        public Location<World> getPointA() {
            return pointA;
        }

        public Location<World> getPointB() {
            return pointB;
        }

        public boolean isComplete() {
            return pointA != null && pointB != null;
        }
    }

    private Map<UUID, PlotSelection> selections = new HashMap<>();

    private PlotSelection getOrCreateSelection(Player player) {
        if (selections.containsKey(player.getUniqueId())) {
            return selections.get(player.getUniqueId());
        } else {
            PlotSelection plotSelection = new PlotSelection();
            selections.put(player.getUniqueId(), plotSelection);
            return plotSelection;
        }
    }

    private void selectPointAAtLocation(Player player, Location<World> location) throws CommandException {
        getOrCreateSelection(player).setPointA(location);
    }

    private void selectPointBAtLocation(Player player, Location<World> location) throws CommandException {
        getOrCreateSelection(player).setPointB(location);
    }

    public void clearSelection(Player player) {
        selections.remove(player.getUniqueId());
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

        int area = 0;

        // TODO

        return area;
    }

    private int getSmallestPlotSelectionSideSize(PlotSelection plotSelection) {
        if (plotSelection == null || !plotSelection.isComplete()) {
            return -1;
        }

        int smallestSide = 0;

        // TODO

        return smallestSide;
    }

    public void selectPointAFromPlayerLocation(Player player) throws CommandException {
        selectPointAAtLocation(player, player.getLocation());
    }

    public void selectPointBFromPlayerLocation(Player player) throws CommandException {
        selectPointBAtLocation(player, player.getLocation());
    }

    /**
     * Validate a plot selection.
     *
     * @param selection the selection to be validated
     * @return true if no exception was thrown
     * @throws CommandException if the plot selection is null, is incomplete ( either point A or point B is null ),
     *                          it's area is greater than the maximum configured, or it's smallest side is smaller than the minimum configured
     */
    public boolean validatePlotSelection(PlotSelection selection) throws CommandException {
        if (selection == null) {
            throw new TownsCommandException("Plot selection is null.");
        }

        if (selection.isComplete()) {
            throw new TownsCommandException("Plot selection is incomplete.");
        }

        int selectionArea = getPlotSelectionArea(selection);
        if (selectionArea > config.MAX_PLOT_AREA) {
            throw new TownsCommandException("Plot selection has an area greater than permitted ( ", selectionArea, " > ", config.MAX_PLOT_AREA, " )");
        }

        int smallestSide = getSmallestPlotSelectionSideSize(selection);
        if (smallestSide < config.MIN_PLOT_SIDE) {
            throw new TownsCommandException("Plot selection has a side smaller than permitted ( ", smallestSide, " < ", config.MIN_PLOT_SIDE, " )");
        }

        return true;
    }

    public PlotSelection getCurrentPlotSelection(Player player) throws CommandException {
        return getOrCreateSelection(player);
    }

}
