package com.atherys.towns.facade;

import com.atherys.towns.api.command.TownsCommandException;
import com.atherys.towns.model.PlotSelection;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.*;

@Singleton
public class PlotSelectionFacade {

    private final Map<UUID, PlotSelection> selections = new HashMap<>();

    @Inject
    TownsMessagingFacade townMsg;

    @Inject
    PlotBorderFacade plotBorderFacade;

    private PlotSelection getOrCreateSelection(Player player) {
        if (selections.containsKey(player.getUniqueId())) {
            return selections.get(player.getUniqueId());
        } else {
            PlotSelection plotSelection = new PlotSelection(false);
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

    public void selectPointAAtLocation(Player player, Location<World> location) {
        getOrCreateSelection(player).setPointA(location);
        checkBorders(player);
        sendPointSelectionMessage(player, location, "A");
    }

    public void selectPointBAtLocation(Player player, Location<World> location) {
        getOrCreateSelection(player).setPointB(location);
        checkBorders(player);
        sendPointSelectionMessage(player, location, "B");
    }

    private void checkBorders(Player player) {
        PlotSelection selection = getOrCreateSelection(player);
        if (selection.isComplete()) {
            plotBorderFacade.refreshBorders(player, player.getLocation());
        }
    }

    private void sendPointSelectionMessage(Player player, Location<World> location, String point) {
        townMsg.info(player, "You have selected point", TextColors.GOLD, " ", point, " ", TextColors.DARK_GREEN,
                "at ", location.getBlockX(), ", ", location.getBlockY(), ", ", location.getBlockZ(), ".");
    }

    /**
     * Validate a plot selection.
     * @param player The player requesting the selection
     */
    public void validatePlayerPlotSelection(Player player) throws TownsCommandException {
        getCompletePlotSelection(player).orElseThrow(() ->
                new TownsCommandException("Plot selection is incomplete.")
        );
    }

    public boolean playerHasValidSelection(Player player) {
        try {
            validatePlayerPlotSelection(player);
        } catch (TownsCommandException e) {
            return false;
        }
        return true;
    }

    public PlotSelection getValidPlayerPlotSelection(Player player) throws TownsCommandException {
        PlotSelection selection = selections.get(player.getUniqueId());
        validatePlayerPlotSelection(player);
        return selection;
    }

    public Optional<PlotSelection> getCompletePlotSelection(Player player) {
        PlotSelection selection = getOrCreateSelection(player);
        return Optional.ofNullable(selection.isComplete() ? selection : null);
    }

    public boolean playerIsSelectingPlot(Player player) {
        return selections.containsKey(player.getUniqueId());
    }

    public void togglePlotSelectionMode(Player player, boolean isCuboid) {
        townMsg.info(player, isCuboid ? "3D" : "2D", " plot selection enabled.");
        getOrCreateSelection(player).setCuboid(isCuboid);
    }
}
