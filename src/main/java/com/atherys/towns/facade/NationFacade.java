package com.atherys.towns.facade;

import com.atherys.core.economy.Economy;
import com.atherys.towns.TownsConfig;
import com.atherys.towns.api.command.TownsCommandException;
import com.atherys.towns.api.permission.nation.NationPermission;
import com.atherys.towns.model.PlotSelection;
import com.atherys.towns.model.entity.Nation;
import com.atherys.towns.model.entity.NationPlot;
import com.atherys.towns.model.entity.Town;
import com.atherys.towns.service.*;
import com.atherys.towns.util.MathUtils;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.service.economy.transaction.TransferResult;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.channel.MessageReceiver;
import org.spongepowered.api.text.format.TextColor;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.spongepowered.api.text.format.TextColors.*;

@Singleton
public class NationFacade implements EconomyFacade {

    @Inject
    private PlotSelectionFacade plotSelectionFacade;

    @Inject
    private TownsConfig config;

    @Inject
    private NationService nationService;

    @Inject
    private RoleService roleService;

    @Inject
    private TownsMessagingFacade townsMsg;

    @Inject
    private TownFacade townFacade;

    @Inject
    private ResidentFacade residentFacade;

    @Inject
    private PermissionFacade permissionFacade;

    @Inject
    private TownsPermissionService townsPermissionService;

    @Inject
    private ResidentService residentService;

    @Inject
    private PlotService plotService;

    NationFacade() {}

    public void createNation(String nationName, Town capital) throws TownsCommandException {
        if (nationName.length() > config.NATION.MAX_NATION_NAME_LENGTH) {
            throw new TownsCommandException("Your name is longer than the maximum (", config.NATION.MAX_NATION_NAME_LENGTH, ").");
        }

        if (nationService.getNationFromName(nationName).isPresent()) {
            throw new TownsCommandException("A nation with that name already exists.");
        }

        if (capital.getNation() != null) {
            throw new TownsCommandException("Town ", capital.getName(), " is already in a nation.");
        }

        nationService.createNation(nationName, capital);
        townsMsg.broadcastInfo("The nation of ", GOLD, nationName, DARK_GREEN, " was created.");
    }

    public void disbandNation(CommandSource source, Nation nation) {
        nationService.disbandNation(nation);
        townsMsg.info(source, "The nation of ", GOLD, nation.getName(), DARK_GREEN, " was disbaned.");
    }

    public void setNationName(Player source, String nationName) throws TownsCommandException {
        Nation nation = getPlayerNation(source);

        nationService.setNationName(nation, nationName);
        townsMsg.info(source, "Nation name set.");
    }

    public void setNationDescription(Player source, Text nationDescription) throws TownsCommandException {
        Nation nation = getPlayerNation(source);

        nationService.setNationDescription(nation, nationDescription);
        townsMsg.info(source, "Nation description set.");
    }


    public void setNationCapital(Player source, Town town) throws TownsCommandException {
        Nation nation = getPlayerNation(source);

        // If the town doesn't have a nation, or the town's nation isn't the nation
        if (town.getNation() == null || town.getNation() != nation) {
            throw new TownsCommandException("Town ", town.getName(), " is not part of your nation.");
        }

        nationService.addTown(nation, town);
        nationService.setCapital(nation, town);
        townsMsg.info(source, "Nation capital set.");
    }

    public void setNationColor(Player source, TextColor color) throws TownsCommandException {
        Nation nation = getPlayerNation(source);

        nationService.setColor(nation, color);
    }

    public void addTownToNation(Nation nation, Town town) throws TownsCommandException {
        if (town.getNation() != null) {
            throw new TownsCommandException("Town ", town.getName(), " is already part of a nation.");
        }

        nationService.addTown(nation, town);
        townsMsg.broadcastNationInfo(nation, "The town ", GOLD, town.getName(), DARK_GREEN,
                " has joined the nation");
    }

    public void removeTownFromNation(Nation nation, Town town) throws TownsCommandException {
        if (!town.getNation().equals(nation)) {
            throw new TownsCommandException("Town ", town.getName(), " is not part of ", nation.getName(), ".");
        }

        if (nation.getCapital().equals(town)) {
            throw new TownsCommandException("Town ", town.getName(), " cannot be removed as it is the capital of ",
                    nation.getName(), ".");
        }

        nationService.removeTown(nation, town);
        townsMsg.broadcastNationInfo(nation, "The town ", GOLD, town.getName(), DARK_GREEN,
                " has left the nation");
        townsMsg.broadcastTownInfo(town, "Your town ", GOLD, town.getName(), DARK_GREEN,
                " has left the nation ", GOLD, nation.getName());
    }

    public void addNationAlly(Player source, Nation ally) throws TownsCommandException {
        Nation nation = getPlayerNation(source);

        if (nation.equals(ally)) {
            throw new TownsCommandException("Cannot add your own nation as an ally.");
        }

        nationService.addNationAlly(nation, ally);
        townsMsg.info(source, "Your nation is now allied with ", GOLD, ally.getName(), DARK_GREEN, ".");
    }

    public void addNationNeutral(Player source, Nation neutral) throws TownsCommandException {
        Nation nation = getPlayerNation(source);

        nationService.addNationNeutral(nation, neutral);
        townsMsg.info(source, "Your nation is now neutral with ", GOLD, neutral.getName(), DARK_GREEN, ".");
    }

    public void addNationEnemy(Player source, Nation enemy) throws TownsCommandException {
        Nation nation = getPlayerNation(source);

        nationService.addNationEnemy(nation, enemy);
        townsMsg.info(source, "Your nation is now enemies with ", GOLD, enemy.getName(), DARK_GREEN, ".");
    }


    public void setNationTax(Player source, double tax) throws TownsCommandException {
        double minMultiplier = config.TAXES.MIN_NATION_TAX_MODIFIER;
        double maxMultiplier = config.TAXES.MAX_NATION_TAX_MODIFIER;

        if (tax > maxMultiplier || tax < minMultiplier) {
            throw new TownsCommandException(Text.of("Tax amount does not meet requirements (", minMultiplier, " - ", maxMultiplier, ")"));
        }

        Nation nation = getPlayerNation(source);
        nationService.setTax(nation, tax);
        townsMsg.info(source, "Nation tax rate changed to: ", GOLD, +tax);
    }

    public void claimNationPlotFromPlayerSelection(Player player, Nation nation) throws CommandException {
        PlotSelection selection = plotSelectionFacade.getValidPlayerPlotSelection(player);

        NationPlot plot = plotService.createNationPlotFromSelection(selection);

        // Check that the plot is not fully contained in an existing plot
        Set<NationPlot> plots = plotService.getNationPlotsByLocation(player.getLocation());

        for (NationPlot other : plots) {
            if (!other.getNation().equals(nation)) {
                throw new TownsCommandException("Plot for nation: ", other.getNation(), " already exists at this location.");
            }

            if (MathUtils.equals(plot, other)) {
                throw new TownsCommandException("Plot with same coordinates already exists.");
            }

            if (MathUtils.contains(other, plot)) {
                throw new TownsCommandException("Plot fits within an existing nation plot.");
            }
        }

        nationService.claimPlotForNation(plot, nation);

        townsMsg.info(player, "Nation Plot claimed.");
    }

    public void removeNationPlotAtPlayerLocation(Player player) throws TownsCommandException {
        Set<NationPlot> plots = plotService.getNationPlotsByLocation(player.getLocation());

        if (plots.size() < 1) {
            throw new TownsCommandException("There is no plot at this location.");
        }

        // Can't unclaim if there are multiple plots
        if (plots.size() > 1) {
            throw new TownsCommandException("Multiple plots exist at this location.");
        }

        nationService.removeNationPlot(plots.iterator().next());

        townsMsg.info(player, "Nation Plot removed.");
    }

    public void sendInfoOnPlotAtPlayerLocation(Player player) throws TownsCommandException {
        Set<NationPlot> plots = plotService.getNationPlotsByLocation(player.getLocation());
        if (plots.size() < 1) {
            throw new TownsCommandException("There is no nation plot at this location.");
        }

        Text.Builder plotText = Text.builder();
        plotText.append(townsMsg.createTownsHeader("Nation Plots"));
        for (NationPlot plot : plots) {

            plotText.append(Text.of(
                    DARK_GREEN, "Plot ID: ", GOLD, plot.getId(), Text.NEW_LINE,
                    DARK_GREEN, "Nation: ",
                    plot.getNation() == null ? Text.of(RED, "None") : Text.of(GOLD, plot.getNation().getName()),
                    Text.NEW_LINE
            ));

            plotText.append(Text.of(DARK_GREEN, "Point A: ", GOLD, "x: ", plot.getSouthWestCorner().getX(), ", z: ", plot.getSouthWestCorner().getY(), Text.NEW_LINE))
                    .append(Text.of(DARK_GREEN, "Point B: ", GOLD, "x: ", plot.getNorthEastCorner().getX(), ", z: ", plot.getNorthEastCorner().getY()))
                    .append(Text.NEW_LINE, Text.NEW_LINE);
        }

        player.sendMessage(plotText.build());
    }

    public void addNationPermission(Player source, User target, NationPermission permission) throws TownsCommandException {
        /*
        Nation nation = getPlayerNation(source);

        permissionFacade.checkPermitted(source, nation, NationPermissions.ADD_PERMISSION,
                "grant permissions for your nation.");

        permissionService.permit(residentService.getOrCreate(target), nation, permission);

        townsMsg.info(source, "Gave ", GOLD, target.getName(), " permission ", GOLD, permission.getId(), ".");
        target.getPlayer().ifPresent(player -> {
            townsMsg.info(player, "You were given the permission ", GOLD, permission.getId(), ".");
        });
        */
    }

    public void removeNationPermission(Player source, User target, NationPermission permission) throws TownsCommandException {
        /*
        Nation nation = getPlayerNation(source);
        permissionFacade.checkPermitted(source, nation, NationPermissions.ADD_PERMISSION,
                "revoke permissions for your nation.");

        permissionService.remove(residentService.getOrCreate(target), nation, permission, true);

        townsMsg.info(source, "Removed permission ", GOLD, permission.getId(), DARK_GREEN, " from ", GOLD, target.getName(), DARK_GREEN, ".");
        target.getPlayer().ifPresent(player -> {
            townsMsg.info(player, "The permission ", GOLD, permission.getId(), DARK_GREEN, " was taken from you.");
        });
        */
    }

    public void addNationRole(Player source, User target, String role) throws TownsCommandException {
        Nation nation = getPlayerNation(source);

        if (partOfSameNation(source, target)) {
            roleService.addNationRole(target, nation, role);
            townsMsg.info(source, GOLD, target.getName(), DARK_GREEN, " was granted the role ", GOLD, role, ".");
        } else {
            throw new TownsCommandException(target.getName(), " is not in the same nation as you!");
        }
    }

    public void removeNationRole(Player source, User target, String role) throws TownsCommandException {
        Nation nation = getPlayerNation(source);

        if (partOfSameNation(source, target)) {
            roleService.removeNationRole(target, nation, role);
            townsMsg.info(source, GOLD, target.getName(), DARK_GREEN, " had the role ", GOLD, role, DARK_GREEN, " revoked.");
        } else {
            throw new TownsCommandException(target.getName(), " is not in the same nation as you!");
        }
    }

    public void depositToNation(Player source, BigDecimal amount) throws TownsCommandException {
        checkEconomyEnabled();

        Nation nation = getPlayerNation(source);

        Optional<TransferResult> result = Economy.transferCurrency(
                source.getUniqueId(),
                nation.getBank().toString(),
                config.DEFAULT_CURRENCY,
                amount,
                Sponge.getCauseStackManager().getCurrentCause()
        );

        if (result.isPresent()) {
            Text feedback = getResultFeedback(
                    result.get().getResult(),
                    Text.of("Deposited ", GOLD, config.DEFAULT_CURRENCY.format(amount), DARK_GREEN, " to the nation."),
                    Text.of("You do not have enough to deposit."),
                    Text.of("Depositing failed.")
            );
            townsMsg.info(source, feedback);
        }
    }

    public void withdrawFromNation(Player source, BigDecimal amount) throws TownsCommandException {
        checkEconomyEnabled();

        Nation nation = getPlayerNation(source);

        Optional<TransferResult> result = Economy.transferCurrency(
                nation.getBank().toString(),
                source.getUniqueId(),
                config.DEFAULT_CURRENCY,
                amount,
                Sponge.getCauseStackManager().getCurrentCause()
        );

        if (result.isPresent()) {
            Text feedback = getResultFeedback(
                    result.get().getResult(),
                    Text.of("Withdrew ", GOLD, config.DEFAULT_CURRENCY.format(amount), DARK_GREEN, " from the nation."),
                    Text.of("The town does not have enough to withdraw."),
                    Text.of("Withdrawing failed.")
            );
            townsMsg.info(source, feedback);
        }
    }

    public void sendPlayerNationInfo(Player player) throws TownsCommandException {
        sendNationInfo(player, getPlayerNation(player));
    }

    public void sendNationInfo(MessageReceiver receiver, Nation nation) {
        Text.Builder nationInfo = Text.builder();

        nationInfo.append(townsMsg.createTownsHeader(nation.getName()));

        if (!nation.getDescription().equals(NationService.DEFAULT_NATION_DESCRIPTION)) {
            nationInfo.append(nation.getDescription(), Text.NEW_LINE);
        }

        Text leader = nation.getLeader() == null ? Text.of("No Leader") : residentFacade.renderResident(nation.getLeader());
        Text capital = nation.getCapital() == null ? Text.of("No Capital") : townFacade.renderTown(nation.getCapital());

        String bank = nation.getBank().toString();
        nationInfo
                .append(Text.of(DARK_GREEN, "Capital: ", GOLD, capital), Text.NEW_LINE)
                .append(Text.of(DARK_GREEN, "Leader: ", GOLD, leader), Text.NEW_LINE)
                .append(townsMsg.renderBank(bank), Text.NEW_LINE);

        Account account = Economy.getAccount(bank).get();
        config.NATION.SECONDARY_CURRENCIES.forEach(currency -> {
            double amount = account.getBalance(currency).doubleValue();
            if (amount > 0) {
                nationInfo.append(Text.of(DARK_GREEN, currency.getPluralDisplayName(), ": ", GOLD, amount, Text.NEW_LINE));
            }
        });

        nationInfo
                .append(Text.of(DARK_GREEN, "Tax Rate: ", GOLD, nation.getTax(), Text.NEW_LINE))
                .append(Text.of(DARK_GREEN, "Population: ", GOLD, nationService.getNationPopulation(nation)), Text.NEW_LINE)
                .append(Text.of(
                        DARK_GREEN, "Towns [", GREEN, nation.getTowns().size(), DARK_GREEN, "]: ",
                        GOLD, townFacade.renderTowns(nation.getTowns())
                ));

        if (!nation.getAllies().isEmpty()) {
            nationInfo.append(Text.of(Text.NEW_LINE, DARK_GREEN, "Allies: ", renderNations(nation.getAllies())));
        }

        if (!nation.getEnemies().isEmpty()) {
            nationInfo.append(Text.of(Text.NEW_LINE, DARK_GREEN, "Enemies: ", renderNations(nation.getEnemies())));
        }

        receiver.sendMessage(nationInfo.build());
    }

    public Text renderNation(Nation nation) {
        if (nation == null) {
            return Text.of(GOLD, "No Nation");
        }

        String leader = nation.getLeader() == null ? "No Leader" : nation.getLeader().getName();

        return Text.builder()
                .append(Text.of(GOLD, nation.getName()))
                .onHover(TextActions.showText(Text.of(
                        GOLD, nation.getName(), Text.NEW_LINE,
                        DARK_GREEN, "Leader: ", GOLD, leader, Text.NEW_LINE,
                        DARK_GREEN, "Towns: ", GOLD, nation.getTowns().size(), Text.NEW_LINE,
                        DARK_GREEN, "Population: ", GOLD, nationService.getNationPopulation(nation), Text.NEW_LINE,
                        townsMsg.renderBank(nation.getBank().toString()), Text.NEW_LINE,
                        DARK_GRAY, "Click to view"
                )))
                .onClick(TextActions.executeCallback(source -> sendNationInfo(source, nation)))
                .build();
    }

    public Text renderNations(Collection<Nation> nations) {
        Text.Builder nationsText = Text.builder();
        int i = 0;
        for (Nation nation : nations) {
            i++;
            nationsText.append(
                    Text.of(renderNation(nation), i == nations.size() ? "" : ", ")
            );
        }

        return nationsText.build();
    }

    public void listNations(MessageReceiver receiver) {
        Text.Builder nationList = Text.builder()
                .append(Text.of(DARK_GRAY, "[]====[ ", GOLD, "Nations", DARK_GRAY, " ]====[]", Text.NEW_LINE));

        int i = 1;
        Collection<Nation> allNations = nationService.getAllNations();
        for (Nation nation : allNations) {
            nationList.append(Text.of(DARK_GREEN, "- ", renderNation(nation)));
            if (i < allNations.size()) {
                nationList.append(Text.NEW_LINE);
            }
            i++;
        }

        receiver.sendMessage(nationList.build());

        //Text.Builder nations = Text.builder().append(Text.of(DARK_GREEN, "Nations: ", renderNations(nationService.getAllNations())));

        //receiver.sendMessage(nations.build());
    }

    private Nation getPlayerNation(Player player) throws TownsCommandException {
        Town town = residentService.getOrCreate(player).getTown();
        if (town == null) {
            throw new TownsCommandException("You are not part of a town!");
        }
        Nation nation = town.getNation();
        if (nation == null) {
            throw new TownsCommandException("Your town is not part of a nation!");
        }
        return nation;
    }

    public Set<Player> getOnlineNationMembers(Nation nation) {
        return nation.getTowns().stream().flatMap(
                town -> townFacade.getOnlineTownMembers(town).stream()).collect(Collectors.toSet());
    }

    public boolean partOfSameNation(User user, User other) {
        Town town = residentService.getOrCreate(user).getTown();
        Town otherTown = residentService.getOrCreate(other).getTown();

        if (town != null && otherTown != null) {
            if ((town.getNation() != null) && (otherTown.getNation() != null)) {
                return town.getNation().equals(otherTown.getNation());
            }
        }

        return false;
    }
}
