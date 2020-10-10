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

import static org.spongepowered.api.text.format.TextColors.DARK_GREEN;
import static org.spongepowered.api.text.format.TextColors.GOLD;

@Singleton
public class PlotSelectionFacade {

    private final Map<UUID, PlotSelection> selections = new HashMap<>();
    private final Set<UUID> pointA = new HashSet<>();
    private final Set<UUID> pointB = new HashSet<>();

    @Inject
    TownsMessagingFacade townMsg;

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
        pointA.remove(player.getUniqueId());
        pointB.remove(player.getUniqueId());
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
            plotBorderFacade.refreshBorders(player, player.getLocation());
        }
    }

    public boolean isSelectingPointA(Player player) {
        return pointA.contains(player.getUniqueId());
    }

    public boolean isSelectingPointB(Player player) {
        return pointB.contains(player.getUniqueId());
    }

    public void startSelectingPointA(Player player) {
        townMsg.info(player, "Right click to select point ", GOLD, "A.");
        pointA.add(player.getUniqueId());
    }

    public void startSelectingPointB(Player player) {
        townMsg.info(player, "Right click to select point ", GOLD, "B.");
        pointB.add(player.getUniqueId());
    }

    public void selectPointAAtLocation(Player player, Location<World> location) {
        getOrCreateSelection(player).setPointA(location);
        pointA.remove(player.getUniqueId());
        checkBorders(player);
        sendPointSelectionMessage(player, "A");
    }

    public void selectPointBAtLocation(Player player, Location<World> location) {
        getOrCreateSelection(player).setPointB(location);
        pointB.remove(player.getUniqueId());
        checkBorders(player);
        sendPointSelectionMessage(player, "B");
    }

    private void sendPointSelectionMessage(Player player, String point) {
        Location<World> location = player.getLocation();
        townMsg.info(player, "You have selected point", GOLD, " ", point, " ", DARK_GREEN,
                "at ", location.getBlockX(), ", ", location.getBlockY(), ", ", location.getBlockZ(), ".");
    }

    /**
     * Validate a plot selection.
     * @param player The player requesting the selection
     */
    public void validatePlayerPlotSelection(Player player) throws TownsCommandException {
        PlotSelection selection = getCurrentPlotSelection(player);
        if (selection == null) {
            throw new TownsCommandException("Plot selection is null");
        }

        if (!selection.isComplete()) {
            throw new TownsCommandException("Plot selection is incomplete.");
        }
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
        PlotSelection selection = getOrCreateSelection(player);
        validatePlayerPlotSelection(player);
        return selection;
    }

    public PlotSelection getCurrentPlotSelection(Player player) {
        return getOrCreateSelection(player);
    }
}
