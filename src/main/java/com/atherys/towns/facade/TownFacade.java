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
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;

import java.util.Optional;

@Singleton
public class TownFacade {

    @Inject
    private PlotSelectionFacade plotSelectionFacade;

    @Inject
    private PlotService plotService;

    @Inject
    private TownService townService;

    @Inject
    private ResidentService residentService;

    @Inject
    private TownsMessagingFacade townsMsg;

    @Inject
    private ResidentFacade residentFacade;

    @Inject
    private PermissionFacade permissionFacade;

    @Inject
    private PermissionService permissionService;

    TownFacade() {
    }

    public void createTown(Player player, String name) throws CommandException {
        if (name == null || name.isEmpty()) {
            throw new TownsCommandException("Must provide a town name.");
        }

        if (hasPlayerTown(player)) {
            throw new TownsCommandException("You are already in a town!");
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

    public void sendTownInfo(Player player, Text townName) throws TownsCommandException {
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

    public void ruinPlayerTown(Player player) throws TownsCommandException {
        Resident resident = residentService.getOrCreate(player);

        if (resident.getTown() == null) {
            throw new TownsCommandException("You are not part of a town.");
        }

        // Only the town leader may remove the town
        if (!residentService.isResidentTownLeader(resident, resident.getTown())) {
            throw new TownsCommandException("Only the town leader may remove the town.");
        }

        townService.removeTown(resident.getTown());
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

            if (town.getPlots().size() == 1) {
                throw new TownsCommandException("You cannot unclaim your last remaining plot.");
            }

            townService.removePlotFromTown(town, plot.get());
            townsMsg.info(source, "Plot abandoned.");
        }
    }

    public void claimTownPlotFromPlayerSelection(Player source) throws CommandException {
        PlotSelection selection = plotSelectionFacade.getValidPlayerPlotSelection(source);

        Town town = getPlayerTown(source);

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

            if (townService.getTownSize(town) + plotService.getPlotArea(plot) > town.getMaxSize()) {
                throw new TownsCommandException("The plot you are claiming is larger than your town's remaining max area.");
            }

            townService.claimPlotForTown(plot, town);

            townsMsg.info(source, "Plot claimed.");
        }
    }

    private void sendTownInfo(Town town, Player player) {
        Text.Builder townText = Text.builder()
                .append(Text.of("Town name: ", town.getName(), Text.NEW_LINE))
                .append(Text.of("Town color: ", town.getColor(), town.getColor().getName(), TextColors.RESET, Text.NEW_LINE))
                .append(Text.of("Town MOTD: ", town.getMotd(), Text.NEW_LINE))
                .append(Text.of("Town description: ", town.getDescription(), Text.NEW_LINE))
                .append(Text.of("Town leader: ", town.getLeader().getName(), Text.NEW_LINE))
                .append(Text.of("Town size: ", townService.getTownSize(town), "/", town.getMaxSize(), Text.NEW_LINE))
                .append(Text.of("Town PvP enabled: ", town.isPvpEnabled(), Text.NEW_LINE))
                .append(Text.of("Town Freely Joinable: ", town.isFreelyJoinable(), Text.NEW_LINE));

        Text.Builder townResidentsText = Text.builder();
        town.getResidents().forEach(resident -> townResidentsText.append(Text.of(resident.getName(), "; ")));
        townText.append(Text.of("Town residents: ", townResidentsText));

        player.sendMessage(townText.build());
    }

    private boolean hasPlayerTown(Player player) {
        return residentFacade.getPlayerTown(player).isPresent();
    }

    public void setTownColor(Player player, TextColor textColor) throws TownsCommandException {
        Town town = null;
        try {
            town = getPlayerTown(player);
        } catch (TownsCommandException e) {
            throw e;
        }
        permissionFacade.isPermitted(player, town, TownPermissions.SET_COLOR);

        townService.setTownColor(town, textColor);
    }

}
