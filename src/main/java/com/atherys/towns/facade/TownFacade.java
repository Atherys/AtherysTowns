package com.atherys.towns.facade;

import com.atherys.core.economy.Economy;
import com.atherys.core.utils.Question;
import com.atherys.towns.TownsConfig;
import com.atherys.towns.api.command.TownsCommandException;
import com.atherys.towns.api.permission.town.TownPermission;
import com.atherys.towns.api.permission.town.TownPermissions;
import com.atherys.towns.model.entity.Plot;
import com.atherys.towns.model.entity.Resident;
import com.atherys.towns.model.entity.Town;
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
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.channel.MessageReceiver;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextStyles;

import java.math.BigDecimal;
import java.util.Collection;
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
    private RoleService roleService;

    @Inject
    private TownsMessagingFacade townsMsg;

    @Inject
    private TownsConfig config;

    @Inject
    private ResidentFacade residentFacade;

    @Inject
    private NationFacade nationFacade;

    @Inject
    private PermissionFacade permissionFacade;

    @Inject
    private TownsPermissionService townsPermissionService;

    TownFacade() {
    }

    public void createTown(Player player, String name) throws CommandException {
        if (name == null || name.isEmpty()) {
            throw new TownsCommandException("Must provide a town name.");
        }

        if (townService.getTownFromName(name).isPresent()) {
            throw new TownsCommandException("A town with that name already exists.");
        }

        if (name.length() > config.TOWN.MAX_TOWN_NAME_LENGTH) {
            throw new TownsCommandException("Your new name is longer than the maximum (", config.TOWN.MAX_TOWN_NAME_LENGTH, ").");
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

            Resident mayor = residentService.getOrCreate(player);

            // create the town
            Town town = townService.createTown(
                    player.getWorld(),
                    player.getTransform(),
                    player,
                    mayor,
                    homePlot,
                    name
            );

            townsPermissionService.updateContexts(player, mayor);

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

    public void setPlayerTownName(Player source, String name) throws TownsCommandException {
        if (name == null || name.isEmpty()) {
            throw new TownsCommandException("Must provide a new town name.");
        }

        Town town = getPlayerTown(source);

        townService.setTownName(town, name);
        townsMsg.info(source, "Town name set.");
    }

    public void setPlayerTownDescription(Player player, Text description) throws TownsCommandException {
        Town town = getPlayerTown(player);

        townService.setTownDescription(town, description);
        townsMsg.info(player, "Town description set.");
    }

    public void setPlayerTownColor(Player player, TextColor color) throws TownsCommandException {
        Town town = getPlayerTown(player);

        townService.setTownColor(town, color);
        townsMsg.info(player, "Town color set.");
    }

    public void setPlayerTownMotd(Player player, Text motd) throws TownsCommandException {
        Town town = getPlayerTown(player);

        townService.setTownMotd(town, motd);
        townsMsg.info(player, "Town motd set.");
    }

    public void setPlayerTownPvp(Player player, boolean pvp) throws TownsCommandException {
        Town town = getPlayerTown(player);

        townService.setTownPvp(town, pvp);
        townsMsg.info(player, "Your town now has PvP ", pvp ? "enabled." : "disabled.");
    }

    public void setPlayerTownJoinable(Player player, boolean joinable) throws TownsCommandException {
        Town town = getPlayerTown(player);

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

        townService.removeTown(town);
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

        if (partOfSameTown(source, invitee)) {
            throw new TownsCommandException(invitee.getName(), " is already part of your town.");
        }

        generateTownInvite(town).pollChat(invitee);
    }

    public void kickFromTown(Player player, User target) throws TownsCommandException {
        Town town = getPlayerTown(player);
        Resident resident = residentService.getOrCreate(target);

        if (town.equals(resident.getTown())) {

            if (town.getLeader().equals(resident)) {
                throw new TownsCommandException("You cannot kick the leader of the town.");
            }

            townService.removeResidentFromTown(player, resident, town);
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
                            townService.addResidentToTown(player, residentService.getOrCreate(player), town);
                            joinTownMessage(player, town);
                        }
                ))
                .addAnswer(Answer.of(
                        Text.of(TextStyles.BOLD, DARK_RED, "Decline"),
                        player -> {}
                ))
                .build();
    }

    public void joinTown(Player player, Town town) throws TownsCommandException {
        if (town.isFreelyJoinable()) {
            townService.addResidentToTown(player, residentService.getOrCreate(player), town);
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
            townService.removeResidentFromTown(player, resident, town);
            townsMsg.info(player, "You have left the town ", GOLD, town.getName(), DARK_GREEN, ".");
        }
    }


    public void addTownPermission(Player source, User target, TownPermission permission) throws TownsCommandException {
        /*
        Town town = getPlayerTown(source);

        permissionFacade.checkPermitted(source, town, TownPermissions.ADD_PERMISSION, "grant permissions.");

        permissionService.permit(residentService.getOrCreate(source), town, permission);

        townsMsg.info(source, "Gave ", GOLD, target.getName(), " permission ", GOLD, permission.getId(), ".");
        target.getPlayer().ifPresent(player -> {
            townsMsg.info(player, "You were given the permission ", GOLD, permission.getId(), ".");
        });
         */
    }

    public void removeTownPermission(Player source, User target, TownPermission permission) throws TownsCommandException {
        /*
        Town town = getPlayerTown(source);

        permissionFacade.checkPermitted(source, town, TownPermissions.REVOKE_PERMISSION, "revoke permissions");

        permissionService.remove(residentService.getOrCreate(target), town, permission, true);
        townsMsg.info(source, "Removed permission ", GOLD, permission.getId(), DARK_GREEN, " from ", GOLD, target.getName(), DARK_GREEN, ".");
        target.getPlayer().ifPresent(player -> {
            townsMsg.info(player, "The permission ", GOLD, permission.getId(), DARK_GREEN, " was taken from you.");
        });
         */
    }

    public void addTownRole(Player source, User target, String role) throws TownsCommandException {
        Town town = getPlayerTown(source);

        if (partOfSameTown(source, target)) {
            roleService.addTownRole(target, town, role);
            townsMsg.info(source, GOLD, target.getName(), DARK_GREEN, " was granted the role ", GOLD, role, ".");
        } else {
            throw new TownsCommandException("");
        }
    }

    public void removeTownRole(Player source, User target, String role) throws TownsCommandException {
        Town town = getPlayerTown(source);

        if (partOfSameTown(source, target)) {
            roleService.removeTownRole(target, town, role);
            townsMsg.info(source, GOLD, target.getName(), DARK_GREEN, " had the role ", GOLD, role, DARK_GREEN, " revoked.");
        } else {
            throw new TownsCommandException("");
        }
    }

    private boolean partOfSameTown(User user, User other) {
        Town town = residentService.getOrCreate(user).getTown();
        Town otherTown = residentService.getOrCreate(other).getTown();
        return (town != null && town.equals(otherTown));
    }

    public void depositToTown(Player player, BigDecimal amount) throws TownsCommandException {
        checkEconomyEnabled();

        Town town = getPlayerTown(player);

        if (config.TOWN.LOCAL_TRANSACTIONS && playerOutsideTown(player, town)) {
            throw new TownsCommandException("You must be inside your town to deposit.");
        }

        Optional<TransferResult> result = Economy.transferCurrency(
                player.getUniqueId(),
                town.getBank().toString(),
                config.DEFAULT_CURRENCY,
                amount,
                Sponge.getCauseStackManager().getCurrentCause()
        );

        if (result.isPresent()) {
            Text feedback = getResultFeedback(
                    result.get().getResult(),
                    Text.of("Deposited", GOLD, config.DEFAULT_CURRENCY.format(amount), DARK_GREEN, " to the town."),
                    Text.of("You do not have enough to deposit."),
                    Text.of("Depositing failed.")
            );
            townsMsg.info(player, feedback);
        }
    }

    public void withdrawFromTown(Player player, BigDecimal amount) throws TownsCommandException {
        checkEconomyEnabled();

        Town town = getPlayerTown(player);

        if (config.TOWN.LOCAL_TRANSACTIONS && playerOutsideTown(player, town)) {
            throw new TownsCommandException("You must be inside your town to deposit.");
        }

        Optional<TransferResult> result = Economy.transferCurrency(
                town.getBank().toString(),
                player.getUniqueId(),
                config.DEFAULT_CURRENCY,
                amount,
                Sponge.getCauseStackManager().getCurrentCause()
        );

        if (result.isPresent()) {
            Text feedback = getResultFeedback(
                    result.get().getResult(),
                    Text.of("Withdrew ", GOLD, config.DEFAULT_CURRENCY.format(amount), DARK_GREEN, " from the town."),
                    Text.of("The town does not have enough to withdraw."),
                    Text.of("Withdrawing failed.")
            );
            townsMsg.info(player, feedback);
        }
    }

    public void setPlayerTownSpawn(Player source) throws TownsCommandException {
        Town town = getPlayerTown(source);

        townsMsg.info(source, "Town spawn set.");
        townService.setTownSpawn(town, source.getTransform());
    }

    private boolean playerOutsideTown(Player player, Town town) {
        return plotService.getPlotByLocation(player.getLocation())
                .map(plot -> plot.getTown().equals(town))
                .orElse(false);
    }

    public void sendTownInfo(Town town, MessageReceiver receiver) {
        Text.Builder townText = Text.builder();

        townText
                .append(townsMsg.createTownsHeader(town.getName()))
                .append(Text.of(GOLD, town.getDescription(), Text.NEW_LINE));

        townText.append(Text.of(
                DARK_GREEN, "Nation: ",
                town.getNation() == null ? Text.of(RED, "None") : nationFacade.renderNation(town.getNation()),
                Text.NEW_LINE
        ));

        townText
                .append(Text.of(DARK_GREEN, "Leader: ", GOLD, residentFacade.renderResident(town.getLeader()), Text.NEW_LINE))
                .append(Text.of(DARK_GREEN, "Size: ", GOLD, townService.getTownSize(town), "/", town.getMaxSize(), Text.NEW_LINE))
                .append(Text.of(DARK_GREEN, "Board: ", GOLD, town.getMotd(), Text.NEW_LINE))
                .append(townsMsg.renderBank(town.getBank().toString()), Text.NEW_LINE)
                .append(Text.of(DARK_GREEN, "PvP: ", townsMsg.renderBoolean(town.isPvpEnabled(), true), DARK_GRAY, " | "))
                .append(Text.of(DARK_GREEN, "Freely Joinable: ", townsMsg.renderBoolean(town.isFreelyJoinable(), true), Text.NEW_LINE));

        townText.append(Text.of(
                DARK_GREEN, "Residents [", GREEN, town.getResidents().size(), DARK_GREEN, "]: ",
                GOLD, residentFacade.renderResidents(town.getResidents())
        ));

        receiver.sendMessage(townText.build());
    }

    public Text renderTown(Town town) {
        if (town == null) {
            return Text.of(GOLD, "No Town");
        }

        return Text.builder()
                .append(Text.of(GOLD, town.getName()))
                .onHover(TextActions.showText(Text.of(
                        GOLD, town.getName(), Text.NEW_LINE,
                        DARK_GREEN, "Nation: ", nationFacade.renderNation(town.getNation()), Text.NEW_LINE,
                        DARK_GREEN, "Leader: ", GOLD, town.getLeader().getName(), Text.NEW_LINE,
                        DARK_GREEN, "Residents: ", GOLD, town.getResidents().size(), Text.NEW_LINE,
                        DARK_GRAY, "Click to view"
                )))
                .onClick(TextActions.executeCallback(source -> sendTownInfo(town, source)))
                .build();
    }

    public Text renderTowns(Collection<Town> towns) {
        Text.Builder townsText = Text.builder();
        int i = 0;
        for (Town town : towns) {
            i++;
            townsText.append(
                    Text.of(renderTown(town), i == towns.size() ? "" : ", ")
            );
        }

        return townsText.build();
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
