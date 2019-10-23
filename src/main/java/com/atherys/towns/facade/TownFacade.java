package com.atherys.towns.facade;

import com.atherys.core.economy.Economy;
import com.atherys.core.utils.Question;
import com.atherys.towns.AtherysTowns;
import com.atherys.towns.TownsConfig;
import com.atherys.towns.api.command.exception.TownsCommandException;
import com.atherys.towns.api.permission.town.TownPermission;
import com.atherys.towns.api.permission.town.TownPermissions;
import com.atherys.towns.entity.Plot;
import com.atherys.towns.entity.Resident;
import com.atherys.towns.entity.Town;
import com.atherys.towns.plot.PlotSelection;
import com.atherys.towns.service.*;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.economy.transaction.TransferResult;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextStyles;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

import static com.atherys.core.utils.Question.Answer;
import static org.spongepowered.api.text.format.TextColors.*;

@Singleton
public class TownFacade implements EconomyFacade {

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
    private TownsConfig config;

    @Inject
    private ResidentFacade residentFacade;

    @Inject
    private PermissionFacade permissionFacade;

    @Inject
    private PermissionService permissionService;

    @Inject
    private TownsConfig townsConfig;

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

            townsMsg.broadcastInfo(
                    player.getName(), " has created the town of ",
                    GOLD, town.getName(), DARK_GREEN, "."
            );
        }
    }

    public void sendTownInfo(Player player) throws TownsCommandException {
        sendTownInfo(getPlayerTown(player), player);
    }

    public void sendTownInfo(Player player, Text townName) throws TownsCommandException {
        if (townName == null || townName.isEmpty()) {
            throw new TownsCommandException("Empty town name.");
        }

        Town town = townService.getTownFromName(townName).orElseThrow(() -> TownsCommandException.townNotFound(townName.toPlain()));

        sendTownInfo(town, player);
    }

    public void setPlayerTownName(Player source, String name) throws TownsCommandException {
        if (name == null || name.isEmpty()) {
            throw new TownsCommandException("Must provide a new town name.");
        }

        Town town = getPlayerTown(source);

        permissionFacade.checkPermitted(source, town, TownPermissions.SET_NAME, "change the town's name.");

        townService.setTownName(town, name);
        townsMsg.info(source, "Town name set.");
    }

    public void setPlayerTownDescription(Player player, Text description) throws TownsCommandException {
        Town town = getPlayerTown(player);

        permissionFacade.checkPermitted(player, town, TownPermissions.SET_DESCRIPTION, "change the town's description.");

        townService.setTownDescription(town, description);
        townsMsg.info(player, "Town description set.");
    }

    public void setPlayerTownColor(Player player, TextColor color) throws TownsCommandException {
        Town town = getPlayerTown(player);

        permissionFacade.checkPermitted(player, town, TownPermissions.SET_COLOR, "change the town's color.");
        townService.setTownColor(town, color);
        townsMsg.info(player, "Town color set.");
    }

    public void setPlayerTownMotd(Player player, Text motd) throws TownsCommandException {
        Town town = getPlayerTown(player);

        permissionFacade.checkPermitted(player, town, TownPermissions.SET_MOTD, "change the town's MOTD.");
        townService.setTownMotd(town, motd);
        townsMsg.info(player, "Town motd set.");
    }

    public void setPlayerTownPvp(Player player, boolean pvp) throws TownsCommandException {
        Town town = getPlayerTown(player);
        permissionFacade.checkPermitted(player, town, TownPermissions.SET_PVP, "change the town's PvP status.");

        townService.setTownPvp(town, pvp);
        townsMsg.info(player, "Your town now has PvP ", pvp ? "enabled." : "disabled.");
    }

    public void setPlayerTownJoinable(Player player, boolean joinable) throws TownsCommandException {
        Town town = getPlayerTown(player);
        permissionFacade.checkPermitted(player, town, TownPermissions.SET_FREELY_JOINABLE, "change the town's joinable status.");

        townService.setTownJoinable(town, joinable);
        townsMsg.info(player, "Your town is now ", joinable ? "freely joinable." : "not freely joinable.");
    }

    public void ruinPlayerTown(Player player) throws TownsCommandException {
        Town town = getPlayerTown(player);
        Resident resident = residentService.getOrCreate(player);

        // Only the town leader may remove the town
        if (!residentService.isResidentTownLeader(resident, town)) {
            throw new TownsCommandException("Only the town leader may remove the town.");
        }

        townService.removeTown(resident.getTown());
        townsMsg.info(player, "Town ruined.");
    }

    public Town getPlayerTown(Player source) throws TownsCommandException {
        Town town = residentService.getOrCreate(source).getTown();

        if (town == null) {
            throw TownsCommandException.notPartOfTown();
        }

        return town;
    }

    public void abandonTownPlotAtPlayerLocation(Player source) throws TownsCommandException {
        Town town = getPlayerTown(source);

        permissionFacade.checkPermitted(source, town, TownPermissions.UNCLAIM_PLOT, "unclaim plots.");

        Plot plot = plotService.getPlotByLocation(source.getLocation()).orElseThrow(() -> {
            return new TownsCommandException("You are not currently standing on a claim area.");
        });

        if (town.getPlots().size() == 1) {
            throw new TownsCommandException("You cannot unclaim your last remaining plot.");
        }

        townService.removePlotFromTown(town, plot);
        townsMsg.info(source, "Plot abandoned.");
    }

    public void claimTownPlotFromPlayerSelection(Player source) throws CommandException {
        PlotSelection selection = plotSelectionFacade.getValidPlayerPlotSelection(source);

        Town town = getPlayerTown(source);

        permissionFacade.checkPermitted(source, town, TownPermissions.CLAIM_PLOT, "claim plots.");

        Plot plot = plotService.createPlotFromSelection(selection);

        if (townService.getTownSize(town) + plotService.getPlotArea(plot) > town.getMaxSize()) {
            throw new TownsCommandException("The plot you are claiming is larger than your town's remaining max area.");
        }

        if (plotService.plotIntersectsAnyOthers(plot)) {
            throw new TownsCommandException("The plot selection intersects with an already-existing plot.");
        }

        if (!plotService.plotBordersTown(town, plot)) {
            throw new TownsCommandException("New plot does not border the town it's being claimed for.");
        }

        townService.claimPlotForTown(plot, town);

        townsMsg.info(source, "Plot claimed.");
    }

    public void inviteToTown(Player source, Player invitee) throws TownsCommandException {
        Town town = getPlayerTown(source);

        permissionFacade.checkPermitted(source, town, TownPermissions.INVITE_RESIDENT, "invite people to the town.");

        if (partOfSameTown(source, invitee)) {
            throw new TownsCommandException(invitee.getName(), " is already part of your town.");
        }

        generateTownInvite(town).pollChat(invitee);
    }

    public void kickFromTown(Player player, User target) throws TownsCommandException {
        Town town = getPlayerTown(player);
        Resident resident = residentService.getOrCreate(target);

        permissionFacade.checkPermitted(player, town, TownPermissions.KICK_RESIDENT, "kick residents.");

        if (town.equals(resident.getTown())) {

            if (town.getLeader().equals(resident)) {
                throw new TownsCommandException("You cannot kick the leader of the town.");
            }

            townService.removeResidentFromTown(resident, town);
            townsMsg.info(player, GOLD, target.getName(), DARK_GREEN, " was kicked from the town.");
        } else {
            throw new TownsCommandException(target.getName(), " is not part of your town.");
        }
    }

    private Question generateTownInvite(Town town) {
        Text townText = Text.of(GOLD, town.getName(), DARK_GREEN, ".");
        Text invitationText = townsMsg.formatInfo(
                "You have been invited to the town ", townText
        );

        return Question.of(invitationText)
                .addAnswer(Answer.of(
                        Text.of(TextStyles.BOLD, DARK_GREEN, "Accept"),
                        player -> {
                            townService.addResidentToTown(residentService.getOrCreate(player), town);
                            joinTownMessage(player, town);
                        }
                ))
                .addAnswer(Answer.of(
                        Text.of(TextStyles.BOLD, DARK_RED, "Decline"),
                        player -> {}
                ))
                .build();
    }

    public void joinTown(Player player, String townName) throws TownsCommandException {
        Town town = getTownFromName(townName);

        if (town.isFreelyJoinable()) {
            townService.addResidentToTown(residentService.getOrCreate(player), town);
            joinTownMessage(player, town);
        } else {
            townsMsg.error(player, town.getName(), " is not freely joinable.");
        }
    }

    public void leaveTown(Player player) throws TownsCommandException {
        Town town = getPlayerTown(player);
        Resident resident = residentService.getOrCreate(player);

        if (town.getLeader().equals(resident)) {
            throw new TownsCommandException("The town leader cannot leave the town.");
        } else {
            townService.removeResidentFromTown(resident, town);
            townsMsg.info(player, "You have left the town ", GOLD, town.getName(), DARK_GREEN, ".");
        }
    }

    public void addTownPermission(Player source, User target, TownPermission permission) throws TownsCommandException {
        Town town = getPlayerTown(source);

        permissionFacade.checkPermitted(source, town, TownPermissions.ADD_PERMISSION, "grant permissions.");

        permissionService.permit(residentService.getOrCreate(source), town, permission);

        townsMsg.info(source, "Gave ", GOLD, target.getName(), " permission ", GOLD, permission.getId(), ".");
        target.getPlayer().ifPresent(player -> {
            townsMsg.info(player, "You were given the permission ", GOLD, permission.getId(), ".");
        });
    }

    public void removeTownPermission(Player source, User target, TownPermission permission) throws TownsCommandException {
        Town town = getPlayerTown(source);

        permissionFacade.checkPermitted(source, town, TownPermissions.REVOKE_PERMISSION, "revoke permissions");

        permissionService.remove(residentService.getOrCreate(target), town, permission, true);
        townsMsg.info(source, "Removed permission ", GOLD, permission.getId(), DARK_GREEN, " from ", GOLD, target.getName(), DARK_GREEN, ".");
        target.getPlayer().ifPresent(player -> {
            townsMsg.info(player, "The permission ", GOLD, permission.getId(), DARK_GREEN, " was taken from you.");
        });
    }

    private boolean partOfSameTown(User user, User other) {
        Town town = residentService.getOrCreate(user).getTown();
        Town otherTown = residentService.getOrCreate(other).getTown();
        return (town != null && town.equals(otherTown));
    }

    public void depositToTown(Player player, BigDecimal amount) throws TownsCommandException {
        checkEconomyEnabled();

        Town town = getPlayerTown(player);

        permissionFacade.checkPermitted(player, town, TownPermissions.DEPOSIT_INTO_BANK, "deposit to the town.");

        if (townsConfig.LOCAL_TRANSACTIONS && !playerInsideTown(player, town)) {
            throw new TownsCommandException("You must be inside your town to deposit.");
        }

        Optional<TransferResult> result = Economy.transferCurrency(
                player.getUniqueId(),
                town.getBank().toString(),
                config.CURRENCY,
                amount,
                Sponge.getCauseStackManager().getCurrentCause()
        );

        if (result.isPresent()) {
            Text feedback = getResultFeedback(
                    result.get().getResult(),
                    Text.of("Deposited", GOLD, config.CURRENCY.format(amount), DARK_GREEN, " to the town."),
                    Text.of("You do not have enough to deposit."),
                    Text.of("Depositing failed.")
            );
            townsMsg.info(player, feedback);
        }
    }

    public void withdrawFromTown(Player player, BigDecimal amount) throws TownsCommandException {
        checkEconomyEnabled();

        Town town = getPlayerTown(player);

        permissionFacade.checkPermitted(player, town, TownPermissions.WITHDRAW_FROM_BANK, "withdraw from the town.");

        if (townsConfig.LOCAL_TRANSACTIONS && !playerInsideTown(player, town)) {
            throw new TownsCommandException("You must be inside your town to deposit.");
        }

        Optional<TransferResult> result = Economy.transferCurrency(
                town.getBank().toString(),
                player.getUniqueId(),
                config.CURRENCY,
                amount,
                Sponge.getCauseStackManager().getCurrentCause()
        );

        if (result.isPresent()) {
            Text feedback = getResultFeedback(
                    result.get().getResult(),
                    Text.of("Withdrew ", GOLD, config.CURRENCY.format(amount), DARK_GREEN, " from the town."),
                    Text.of("The town does not have enough to withdraw."),
                    Text.of("Withdrawing failed.")
            );
            townsMsg.info(player, feedback);
        }
    }

    public void setPlayerTownSpawn(Player source) throws TownsCommandException {
        Town town = getPlayerTown(source);

        permissionFacade.checkPermitted(source, town, TownPermissions.SET_SPAWN, "set the town spawn.");
        townsMsg.info(source, "Town spawn set.");
        townService.setTownSpawn(town, source.getTransform());
    }

    private boolean playerInsideTown(Player player, Town town) {
        return plotService.getPlotByLocation(player.getLocation())
                .map(plot -> plot.getTown().equals(town))
                .orElse(false);
    }

    private void sendTownInfo(Town town, Player player) {
        Text.Builder townText = Text.builder()
                .append(Text.of("Town name: ", town.getName(), Text.NEW_LINE))
                .append(Text.of("Town color: ", town.getColor(), town.getColor().getName(), RESET, Text.NEW_LINE))
                .append(Text.of("Town MOTD: ", town.getMotd(), Text.NEW_LINE))
                .append(Text.of("Town description: ", town.getDescription(), Text.NEW_LINE))
                .append(Text.of("Town leader: ", town.getLeader().getName(), Text.NEW_LINE))
                .append(Text.of("Town size: ", townService.getTownSize(town), "/", town.getMaxSize(), Text.NEW_LINE))
                .append(Text.of("Town PvP enabled: ", town.isPvpEnabled(), Text.NEW_LINE))
                .append(Text.of("Town Freely Joinable: ", town.isFreelyJoinable(), Text.NEW_LINE));

        if (AtherysTowns.economyIsEnabled()) {
            Economy.getAccount(town.getBank().toString()).ifPresent(account -> {
                townText.append(Text.of("Town balance: ", config.CURRENCY.format(account.getBalance(config.CURRENCY)), Text.NEW_LINE));
            });
        }

        Text.Builder townResidentsText = Text.builder();
        town.getResidents().forEach(resident -> townResidentsText.append(Text.of(resident.getName(), ", ")));
        townText.append(Text.of("Town residents: ", townResidentsText));

        player.sendMessage(townText.build());
    }

    public Town getTownFromName(String townName) throws TownsCommandException {
        return townService.getTownFromName(Text.of(townName))
                .orElseThrow(() -> TownsCommandException.townNotFound(townName));
    }

    private void joinTownMessage(Player player, Town town) {
        //TODO: Send message to whole town
        Text townText = Text.of(GOLD, town.getName(), DARK_GREEN, ".");
        townsMsg.info(player, "You have joined the town of ", townText);
    }

    private boolean hasPlayerTown(Player player) {
        return residentFacade.getPlayerTown(player).isPresent();
    }
}
