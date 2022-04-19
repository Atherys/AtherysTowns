package com.atherys.towns.facade;

import com.atherys.core.economy.Economy;
import com.atherys.core.utils.Question;
import com.atherys.core.utils.Question.Answer;
import com.atherys.core.utils.UserUtils;
import com.atherys.towns.AtherysTowns;
import com.atherys.towns.TownsConfig;
import com.atherys.towns.api.command.TownsCommandException;
import com.atherys.towns.api.permission.town.TownPermission;
import com.atherys.towns.api.permission.town.TownPermissions;
import com.atherys.towns.integration.AtherysPartiesIntegration;
import com.atherys.towns.model.PlotSelection;
import com.atherys.towns.model.entity.*;
import com.atherys.towns.service.*;
import com.atherys.towns.util.MathUtils;
import com.flowpowered.math.vector.Vector2i;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.service.economy.transaction.TransactionResult;
import org.spongepowered.api.service.economy.transaction.TransferResult;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.channel.MessageReceiver;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.util.AABB;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

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
    private PollFacade pollFacade;

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
    private TaxFacade taxFacade;

    @Inject
    private PermissionFacade permissionFacade;

    @Inject
    private PlotBorderFacade plotBorderFacade;

    @Inject
    private TownsPermissionService townsPermissionService;

    @Inject
    private TaxService taxService;

    TownFacade() {
    }

    public boolean isTownTaxDue(Town town) {
        return town.getTaxFailedCount() > 0;
    }

    public void createTownOrPoll(Player player, String townName) throws CommandException {
        if (townName == null || townName.isEmpty()) {
            throw new TownsCommandException("Must provide a town name.");
        }

        if (townService.getTownFromName(townName).isPresent()) {
            throw new TownsCommandException("A town with that name already exists.");
        }

        if (townName.length() > config.TOWN.MAX_TOWN_NAME_LENGTH) {
            throw new TownsCommandException("Your new name is longer than the maximum (", config.TOWN.MAX_TOWN_NAME_LENGTH, ").");
        }

        Resident resident = residentService.getOrCreate(player);
        if (hasPlayerTown(player) && residentService.isResidentTownLeader(resident, resident.getTown())) {
            throw new TownsCommandException("You are already a town leader!");
        }

        Nation nation = null;
        Optional<NationPlot> plot = plotService.getNationPlotsByLocation(player.getLocation()).stream().findFirst();
        if (plot.isPresent()) {
            nation = plot.get().getNation();
        }

        if (nation == null && !permissionFacade.isPermitted(player, TownPermissions.CREATE_WITHOUT_NATION)) {
            throw new TownsCommandException("You are not permitted to create a town outside of a nation!");
        }

        PlotSelection selection = plotSelectionFacade.getValidPlayerPlotSelection(player);
        TownPlot homePlot = plotService.createTownPlotFromSelection(selection);
        validateNewTownPlot(homePlot, player, player.getLocation());

        Optional<TownPlot> closestPlot = plotService.getClosestTownPlot(homePlot);

        if (closestPlot.isPresent()) {
            int centerX = (homePlot.getNorthEastCorner().getX() + homePlot.getSouthWestCorner().getX()) / 2;
            int centerZ = (homePlot.getNorthEastCorner().getY() + homePlot.getSouthWestCorner().getY()) / 2;
            double distance = MathUtils.getDistanceToPlotSquared(Vector2i.from(centerX, centerZ), closestPlot.get());
            if (distance < Math.pow(config.TOWN.MIN_CREATION_DISTANCE, 2)) {
                throw new TownsCommandException("This plot is too close to an existing town. (Min distance " + config.TOWN.MIN_CREATION_DISTANCE + ")");
            }
        }

        if (!Sponge.getPluginManager().isLoaded("atherysparties") ||
                permissionFacade.isPermitted(player, TownPermissions.CREATE_WITHOUT_PARTY)) {
            createTown(player, townName, homePlot, nation);
        } else if (AtherysPartiesIntegration.playerHasParty(player)) {
            Set<Player> partyMembers = AtherysPartiesIntegration.fetchPlayerPartyMembers(player);
            if (partyMembers.size() < config.MIN_RESIDENTS_TOWN_CREATE) {
                throw new TownsCommandException("Your party does not have enough members (Min: " + config.MIN_RESIDENTS_TOWN_CREATE + ").");
            }
            partyMembers.removeAll(partyMembers.stream().filter(this::isLeaderOfPlayerTown).collect(Collectors.toSet()));
            pollFacade.sendCreateTownPoll(townName, partyMembers, player, homePlot, nation);
        }
        else {
            throw new TownsCommandException("You require a party to form a town!");
        }
    }

    public Town createTown(Player player, String name, TownPlot homePlot, @Nullable Nation nation) throws CommandException {
        Resident mayor = residentService.getOrCreate(player);

        // Doing this check here, so that we don't need to handle refund logic in a few places
        if (AtherysTowns.economyIsEnabled()) {
            Account account = Economy.getAccount(player.getUniqueId()).get();
            Cause cause = Sponge.getCauseStackManager().getCurrentCause();
            TransactionResult result = account.withdraw(config.DEFAULT_CURRENCY,
                    BigDecimal.valueOf(config.TOWN.CREATION_COST), cause);
            if (result.getResult() == ResultType.FAILED || result.getResult() == ResultType.ACCOUNT_NO_FUNDS) {
                throw new TownsCommandException("You lack the funds to create a town. ", config.TOWN.CREATION_COST,
                        " ", config.DEFAULT_CURRENCY.getPluralDisplayName(), " are required.");
            }
        }

        // create the town
        Town town = townService.createTown(
                player,
                homePlot,
                name,
                nation
        );

        townsPermissionService.updateContexts(player, mayor);

        sendTownInfo(town, player);

        townsMsg.broadcastInfo(
                GOLD, player.getName(), DARK_GREEN, " has created the town of ",
                GOLD, town.getName(), DARK_GREEN, "."
        );
        plotSelectionFacade.clearSelection(player);
        return town;
    }

    public void grantTown(User player, User target) throws TownsCommandException {
        Town town = getPlayerTown(player);
        Resident newMayor = residentService.getOrCreate(target);
        Resident oldMayor = residentService.getOrCreate(player);

        if (residentService.isResidentTownLeader(oldMayor, town) && partOfSameTown(player, target)) {
            roleService.removeTownRole(player, town, config.TOWN.TOWN_LEADER_ROLE);
            townService.setTownLeader(town, newMayor);
            roleService.addTownRole(target, town, config.TOWN.TOWN_LEADER_ROLE);
            townsMsg.broadcastTownInfo(town, GOLD, target.getName(), DARK_GREEN, " is now the mayor of ", GOLD, town.getName(), ".");
        } else {
            throw new TownsCommandException("The player you are granting leadership to is either not in your town, or you are not the town leader.");
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

        if (isTownTaxDue(town)) {
            throw new TownsCommandException("Unable to change PvP status as taxes are unpaid!");
        }

        townService.setTownPvp(town, pvp);
        townsMsg.info(player, "Your town now has PvP ", pvp ? "enabled." : "disabled.");
    }

    public void setPlayerTownJoinable(Player player, boolean joinable) throws TownsCommandException {
        Town town = getPlayerTown(player);

        townService.setTownJoinable(town, joinable);
        townsMsg.info(player, "Your town is now ", joinable ? "freely joinable." : "not freely joinable.");
    }

    public void setPlayerTownMobs(Player playerb, boolean mobs) throws TownsCommandException {
        Town town = getPlayerTown(playerb);

        townService.setTownMobs(town, mobs);
        townsMsg.info(playerb, "Your town now has hostile mobs ", mobs ? "enabled." : "disabled.");
    }

    public void ruinPlayerTown(Player player) throws TownsCommandException {
        Town town = getPlayerTown(player);
        Resident resident = residentService.getOrCreate(player);

        // Towns that are the capital of a nation cannot be ruined.
        if (town.getNation() != null && town.getNation().getCapital().equals(town)) {
            throw new TownsCommandException("Nation capitals cannot be ruined.");
        }

        // Only the town leader may remove the town
        if (!residentService.isResidentTownLeader(resident, town)) {
            throw new TownsCommandException("Only the town leader may remove the town.");
        }

        Question confirmation = Question.of(Text.of(DARK_GREEN, "Are you sure you want to delete your town?"))
                .addAnswer(Answer.of(Text.of(DARK_GREEN, "Yes"), p -> {
                    townService.removeTown(town);
                    plotBorderFacade.removeBordersForTown(town);
                    townsMsg.info(player, "Town ruined.");
                }))
                .addAnswer(Answer.of(Text.of(RED, "No"), p -> {
                    townsMsg.error(p, "Town deletion cancelled.");
                }))
                .build();

        confirmation.pollChat(player);
    }

    public Town getPlayerTown(User source) throws TownsCommandException {
        Town town = residentService.getOrCreate(source).getTown();

        if (town == null) {
            throw TownsCommandException.notPartOfTown();
        }

        return town;
    }

    /**
     * Validate a TownPlot meets the sizing rules
     * @param plot The plot to validate
     * @param player The player requesting validation
     * @param location location when validation was requested
     */
    public void validateNewTownPlot(TownPlot plot, Player player, Location<World> location) throws TownsCommandException {
        int plotArea = MathUtils.getArea(plot);
        if (plotArea > config.TOWN.MAX_PLOT_AREA) {
            throw new TownsCommandException("Plot selection has an area greater than permitted ( ", plotArea, " > ", config.TOWN.MAX_PLOT_AREA, " )");
        }

        int smallestSide = MathUtils.getShortestSide(plot);
        if (smallestSide < config.TOWN.MIN_PLOT_SIDE - 1) {
            throw new TownsCommandException("Plot selection has a side smaller than permitted ( ", smallestSide, " < ", config.TOWN.MIN_PLOT_SIDE, " )");
        }

        if (plotService.townPlotIntersectAnyOthers(plot)) {
            throw new TownsCommandException("The plot selection intersects with an already-existing plot.");
        }

        if (!plotService.isLocationWithinPlot(location, plot)) {
            throw new TownsCommandException("You must be within your plot selection!");
        }

        Town town = residentService.getOrCreate(player).getTown();
        if (town != null) {
            if (isTownTaxDue(town)) {
                throw new TownsCommandException("Plot claiming has been disabled due to unpaid taxes!");
            }

            if (townService.getTownSize(town) + MathUtils.getArea(plot) > town.getMaxSize()) {
                throw new TownsCommandException("The plot you are claiming is larger than your town's remaining max area.");
            }

            if (!plotService.townPlotBordersTown(town, plot)) {
                throw new TownsCommandException("New plot does not border the town it's being claimed for.");
            }
        }

        // Plot must be completely contained within the nation
        Optional<NationPlot> nPlot = plotService.getNationPlotsByTownPlot(plot);
        if (nPlot.isPresent()) {
            if (!MathUtils.rectangleContainedInSet(plot, nPlot.get().getNation().getPlots())){
                throw new TownsCommandException("New plot is not contained completely within the nation.");
            }
        }
    }

    public TownPlot validateNewCuboidTownPlot(TownPlot plot, Player player, Location<World> location) throws TownsCommandException {
        AABB cuboid = plot.asAABB();

        int shortestSide = MathUtils.getShortestSide(cuboid);
        if (shortestSide < config.TOWN.MIN_CUBOID_PLOT_SIDE - 1) {
            throw new TownsCommandException("Plot selection has a side smaller than permitted ( ", shortestSide, " < ", config.TOWN.MIN_PLOT_SIDE, " )");
        }

        Town town = residentService.getOrCreate(player).getTown();

        if (isTownTaxDue(town)) {
            throw new TownsCommandException("Plot claiming has been disabled due to unpaid taxes!");
        }

        TownPlot containingPlot = plotService.getTownPlotContainingPlot(plot, town).orElseThrow(() ->
                new TownsCommandException("New cuboid plot is not contained within an existing plot.")
        );

        if (plotService.aabbIntersectAnyCuboidPlots(cuboid, containingPlot)) {
            throw new TownsCommandException("New cuboid plot intersects other cuboid plot(s).");
        }

        return containingPlot;
    }

    public boolean isValidNewTownPlot(TownPlot plot, Player player, Location<World> location, boolean messageUser) {
        try {
            validateNewTownPlot(plot, player, location);
        } catch (TownsCommandException e) {
            if (messageUser && e.getText() != null) player.sendMessage(e.getText());
            return false;
        }
        return true;
    }

    public void abandonTownPlotAtPlayerLocation(Player source) throws TownsCommandException {
        Town town = getPlayerTown(source);
        TownPlot plot = plotService.getTownPlotByLocation(source.getLocation()).orElseThrow(() -> {
            return new TownsCommandException("You are not currently standing in a plot.");
        });

        if (town.getPlots().size() == 1) {
            throw new TownsCommandException("You cannot unclaim your last remaining plot.");
        }

        if (!plot.isCuboid() && townService.checkPlotRemovalCreatesOrphans(town, plot)) {
            throw new TownsCommandException("You cannot unclaim a plot that would result in orphaned plots.");
        }

        townService.removePlotFromTown(town, plot);
        plotBorderFacade.removeSelectionBorder(source, plot);
        townsMsg.info(source, "Plot abandoned.");
    }

    public void claimTownPlotFromPlayerSelection(Player source) throws CommandException {
        PlotSelection selection = plotSelectionFacade.getValidPlayerPlotSelection(source);

        Town town = getPlayerTown(source);
        TownPlot plot = plotService.createTownPlotFromSelection(selection);
        if (plot.isCuboid()) {
            TownPlot containingPlot = validateNewCuboidTownPlot(plot, source, source.getLocation());
            townService.claimCuboidPlotForTown(plot, containingPlot);
        } else {
            validateNewTownPlot(plot, source, source.getLocation());
            townService.claimPlotForTown(plot, town);
        }

        plotSelectionFacade.clearSelection(source);
        townsMsg.info(source, "Plot claimed.");
    }

    public void claimTownPlotWithoutThrowing(Player source) {
        try {
            claimTownPlotFromPlayerSelection(source);
        } catch (CommandException e) {
            townsMsg.error(source, e.getText());
        }
    }

    public void inviteToTown(Player source, Player invitee) throws TownsCommandException {
        Town town = getPlayerTown(source);

        if (partOfSameTown(source, invitee)) {
            throw new TownsCommandException(invitee.getName(), " is already part of your town.");
        }

        generateTownInvite(town).pollChat(invitee);
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
                        player -> {
                        }
                ))
                .build();
    }

    public void kickFromTown(Player player, User target) throws TownsCommandException {
        Town town = getPlayerTown(player);
        Resident resident = residentService.getOrCreate(target);

        if (town.equals(resident.getTown())) {

            if (town.getLeader() != null && town.getLeader().equals(resident)) {
                throw new TownsCommandException("You cannot kick the leader of the town.");
            }

            townService.removeResidentFromTown(player, resident, town);
            townsMsg.info(player, GOLD, target.getName(), DARK_GREEN, " was kicked from the town.");
        } else {
            throw new TownsCommandException(target.getName(), " is not part of your town.");
        }
    }

    public void joinTown(User player, Town town) throws TownsCommandException {
        if (!town.isFreelyJoinable()) {
            throw new TownsCommandException(Text.of(town.getName(), " is not freely joinable."));
        }

        townService.addResidentToTown(player, residentService.getOrCreate(player), town);
        joinTownMessage(player, town);
    }

    public void leaveTown(User user) throws TownsCommandException {
        Town town = getPlayerTown(user);
        Resident resident = residentService.getOrCreate(user);

        if (town.getLeader() != null && town.getLeader().equals(resident)) {
            throw new TownsCommandException("The town leader cannot leave the town.");
        }

        townService.removeResidentFromTown(user, resident, town);

        if (user instanceof Player) {
            townsMsg.info((Player) user, "You have left the town ", GOLD, town.getName(), DARK_GREEN, ".");
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

        if (config.TOWN.LOCAL_TRANSACTIONS && !playerInsideTown(player, town)) {
            throw new TownsCommandException("You must be inside your town to deposit.");
        }

        TransferResult result = depositToTown(player, town, amount);

        Text feedback = getResultFeedback(
                result.getResult(),
                Text.of("Deposited ", GOLD, config.DEFAULT_CURRENCY.format(amount), DARK_GREEN, " to the town."),
                Text.of("You do not have enough to deposit."),
                Text.of("Depositing failed.")
        );

        townsMsg.info(player, feedback);
    }

    public TransferResult depositToTown(Player player, Town town, BigDecimal amount) throws TownsCommandException {
        return Economy.transferCurrency(
                player.getUniqueId(),
                town.getBank().toString(),
                config.DEFAULT_CURRENCY,
                amount,
                Sponge.getCauseStackManager().getCurrentCause()
        ).orElseThrow(() ->
                new TownsCommandException("Transaction failed. Please report this.")
        );
    }

    public void withdrawFromTown(Player player, BigDecimal amount) throws TownsCommandException {
        checkEconomyEnabled();

        Town town = getPlayerTown(player);

        if (config.TOWN.LOCAL_TRANSACTIONS && !playerInsideTown(player, town)) {
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
        Optional<TownPlot> plot = plotService.getTownPlotByLocation(source.getLocation());

        if (!plot.isPresent()) {
            throw new TownsCommandException("Current location is not part of your town");
        }

        if (!plot.get().getTown().equals(town)) {
            throw new TownsCommandException("Current location is not part of your town");
        }

        townsMsg.info(source, "Town spawn set.");
        townService.setTownSpawn(town, source.getTransform());
    }

    private boolean playerInsideTown(Player player, Town town) {
        return plotService.getTownPlotByLocation(player.getLocation())
                .map(plot -> plot.getTown().equals(town))
                .orElse(false);
    }

    public Set<Player> getOnlineTownMembers(Town town) {
        Set<UUID> townResidents = town.getResidents().stream().map(Resident::getId).collect(Collectors.toSet());
        return Sponge.getServer().getOnlinePlayers().stream().filter(
                player -> townResidents.contains(player.getUniqueId())).collect(Collectors.toSet());
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
                .append(Text.of(DARK_GREEN, "Leader: ", GOLD, town.getLeader().isFake() ? "None" : residentFacade.renderResident(town.getLeader()), Text.NEW_LINE))
                .append(Text.of(DARK_GREEN, "Size: ", GOLD, townService.getTownSize(town), "/", town.getMaxSize(), Text.NEW_LINE))
                .append(Text.of(DARK_GREEN, "Board: ", GOLD, town.getMotd(), Text.NEW_LINE))
                .append(townsMsg.renderBank(town.getBank().toString()), Text.NEW_LINE)
                .append(taxFacade.renderTax(town))
                .append(Text.of(DARK_GREEN, "PvP: ", townsMsg.renderBoolean(town.isPvpEnabled(), true), DARK_GRAY, " | "))
                .append(Text.of(DARK_GREEN, "Freely Joinable: ", townsMsg.renderBoolean(town.isFreelyJoinable(), true), Text.NEW_LINE))
                .append(Text.of(
                    DARK_GREEN, "Residents [", GREEN, town.getResidents().size() - (town.getLeader().isFake() ? 1 : 0), DARK_GREEN, "]: ",
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
                        DARK_GREEN, "Leader: ", GOLD, town.getLeader() != null ? town.getLeader().getName() : "None", Text.NEW_LINE,
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

    public void onPlayerDamage(DamageEntityEvent event, Player attacker, Player target) {
        Optional<TownPlot> attackerPlot = plotService.getTownPlotByLocation(attacker.getLocation());
        Optional<TownPlot> targetPlot = plotService.getTownPlotByLocation(target.getLocation());
        //If the attacker and the target are not in plots, then return.
        if (!attackerPlot.isPresent() && !targetPlot.isPresent()) {
            return;
        }
        //If the target is in a plot, set cancelled to opposite of pvpEnabled for that town.
        if (targetPlot.isPresent()) {
            event.setCancelled(!targetPlot.get().getTown().isPvpEnabled());
            return;
        }
        //If none of the above statements catch, the attacker must be in a plot. Set to opposite of pvpEnabled for that town.
        event.setCancelled(!attackerPlot.get().getTown().isPvpEnabled());
    }

    private void joinTownMessage(User player, Town town) {
        townsMsg.broadcastTownInfo(town, player.getName(), " has joined the town");
    }

    private boolean isLeaderOfPlayerTown(Player player) {
        Resident resident = residentService.getOrCreate(player);
        if (resident.getTown() != null) {
            return resident.equals(resident.getTown().getLeader());
        }
        return false;
    }

    private boolean hasPlayerTown(Player player) {
        return residentFacade.getPlayerTown(player).isPresent();
    }

    public void payTownDebt(Player player) throws TownsCommandException {
        Town town = getPlayerTown(player);

        if (town.getDebt() == 0) {
            throw new TownsCommandException("You have no debt to pay!");
        }

        Account townBank = Economy.getAccount(town.getBank().toString()).get();
        double townBalance = townBank.getBalance(config.DEFAULT_CURRENCY).doubleValue();

        if (town.getDebt() > townBalance) {
            throw new TownsCommandException("Town bank does not have enough money to pay your debt!");
        }

        double voided = town.getDebt() * config.TAXES.VOID_RATE;

        taxService.payTaxes(town, town.getDebt() - voided, voided);
        taxService.setTaxesPaid(town, true);
        townsMsg.info(player, "Tax debt has been paid off! All town features have been re-enabled!");
    }

    public void recalculateTownSizes() {
        if (!config.TOWN_SIZE_AUTOMATION.IS_ENABLED) {
            return;
        }

        townService.fetchAllTowns().forEach(this::recalculateTownSize);
    }

    private void recalculateTownSize(Town town) {
        int numberOfActiveResidents = (int) town.getResidents().stream()
                .filter(resident -> residentFacade.isResidentActive(resident))
                .count();

        int newTownMaxArea = config.TOWN_SIZE_AUTOMATION.AREA_GRANTED_PER_ACTIVE_RESIDENT * numberOfActiveResidents;

        town.setMaxSize(newTownMaxArea);
    }

    public void setTownTaxable(Town town, boolean taxable) {
        townService.setTownTaxable(town, taxable);
    }

    /**
     * Cases:<br>
     * 1. Remove leader of town and leave town leaderless<br>
     *  1.1. Town has leader<br>
     *   - Remove leader resident as leader ( but keep in town )<br>
     *  1.2 Town does not have leader<br>
     *   - Do nothing<br><br>
     * 2. Set player as mayor of town<br>
     *  2.1. Player is not part of town<br>
     *   - Join player to the town<br>
     *   - grant the town to them<br>
     *  2.2. Player is part of town<br>
     *   2.2.1. Player is part of same town<br>
     *    - Grant the town to them<br>
     *   2.2.2. Player is not part of same town<br>
     *    - Make player leave their own town ( fails if they are leader )<br>
     *    - Join player to the requested town<br>
     *    - Grant the town to them<br>
     * @param town
     * @param user
     * @throws CommandException
     */
    public void overrideLeader(Town town, @Nullable User user) throws CommandException {
        if (town == null) {
            throw new TownsCommandException("No town provided");
        }

        if (user == null) {
            removeTownLeader(town);
        } else {
            replaceTownLeader(town, user);
        }
    }

    /**
     * 1. Remove leader of town and leave town leaderless ( create fake resident with fake name )<br>
     *  1.1. Town has leader<br>
     *   - Remove leader resident as leader ( but keep in town )<br>
     *  1.2 Town does not have leader<br>
     * @param town
     * @throws TownsCommandException
     */
    private void removeTownLeader(Town town) throws TownsCommandException {
        if (town.getLeader().isFake()) {
            throw new TownsCommandException("This town does not have a leader.");
        }

        Resident leader = town.getLeader();

        UserUtils.getUser(leader.getId()).ifPresent(user -> roleService.removeTownRole(user, town, config.TOWN.TOWN_LEADER_ROLE));

        Resident fakeResident = residentService.createFakeResident("None");

        townService.addResidentToTown(null, fakeResident, town);
        townService.setTownLeader(town, fakeResident);
    }

    /**
     * 2. Set player as mayor of town<br>
     *  2.1. Player is not part of town<br>
     *   - Join player to the town<br>
     *   - grant the town to them<br>
     *  2.2. Player is part of town<br>
     *   2.2.1. Player is part of same town<br>
     *    - Grant the town to them<br>
     *   2.2.2. Player is not part of same town<br>
     *    - Make player leave their own town ( fails if they are leader )<br>
     *    - Join player to the requested town<br>
     *    - Grant the town to them<br>
     *
     * @param town
     * @param user
     */
    private void replaceTownLeader(Town town, User user) throws TownsCommandException {
        Resident userResident = residentService.getOrCreate(user);

        if (userResident.getTown() == null) {
            townService.addResidentToTown(user, userResident, town);
        }

        if (userResident.getTown() != null && !userResident.getTown().getId().equals(town.getId())) {
            Resident userTownLeader = userResident.getTown().getLeader();

            if (userTownLeader.getId().equals(user.getUniqueId())) {
                throw new TownsCommandException("That user is already the leader of another town");
            }

            townService.removeResidentFromTown(user, userResident, town);
            townService.addResidentToTown(user, userResident, town);
        }

        Optional<? extends User> currentTownLeader = UserUtils.getUser(town.getLeader().getId());

        if (currentTownLeader.isPresent()) {
            this.grantTown(currentTownLeader.get(), user);
        } else {
            townService.setTownLeader(town, userResident);
            roleService.addTownRole(user, town, config.TOWN.TOWN_LEADER_ROLE);
        }
    }
}
