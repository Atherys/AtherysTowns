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
    private final Map<UUID, Boolean> selectionModes = new HashMap<>();

    @Inject
    TownsMessagingFacade townMsg;

    @Inject
    PlotBorderFacade plotBorderFacade;

    private PlotSelection getOrCreateSelection(Player player) {
        PlotSelection selection;
        if (selections.containsKey(player.getUniqueId())) {
            selection = selections.get(player.getUniqueId());
        } else {
            selection = new PlotSelection();
            selections.put(player.getUniqueId(), selection);
        }

        selection.setCuboid(selectionModes.getOrDefault(player.getUniqueId(), false));
        return selection;
    }

    public void clearSelection(Player player) {
        selections.remove(player.getUniqueId());
        plotBorderFacade.refreshBorders(player, player.getLocation());
        townMsg.info(player, "Your plot selection has been cleared.");
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

    public PlotSelection getValidPlayerPlotSelection(Player player) throws TownsCommandException {
        PlotSelection selection = selections.get(player.getUniqueId());
        validatePlayerPlotSelection(player);
        return selection;
    }

    /**
     * Returns a player's {@link PlotSelection} if it has both points selected, or an empty optional.
     */
    public Optional<PlotSelection> getCompletePlotSelection(Player player) {
        PlotSelection selection = getOrCreateSelection(player);
        return Optional.ofNullable(selection.isComplete() ? selection : null);
    }

    /**
     * Sets a player's selection mode: whether they are selecting a 3D plot or not.
     */
    public void togglePlotSelectionMode(Player player, boolean isCuboid) {
        selectionModes.put(player.getUniqueId(), isCuboid);
        townMsg.info(player, isCuboid ? "3D" : "2D", " plot selection enabled.");
    }
}
