package com.atherys.towns.facade;

import com.atherys.core.economy.Economy;
import com.atherys.towns.TownsConfig;
import com.atherys.towns.api.command.TownsCommandException;
import com.atherys.towns.api.permission.nation.NationPermission;
import com.atherys.towns.api.permission.nation.NationPermissions;
import com.atherys.towns.entity.Nation;
import com.atherys.towns.entity.Town;
import com.atherys.towns.service.NationService;
import com.atherys.towns.service.PermissionService;
import com.atherys.towns.service.ResidentService;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.economy.transaction.TransferResult;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.channel.MessageReceiver;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Optional;

import static org.spongepowered.api.text.format.TextColors.*;

@Singleton
public class NationFacade implements EconomyFacade {
    @Inject
    TownsConfig config;

    @Inject
    NationService nationService;

    @Inject
    TownsMessagingFacade townsMsg;

    @Inject
    TownFacade townFacade;

    @Inject
    ResidentFacade residentFacade;

    @Inject
    PermissionFacade permissionFacade;

    @Inject
    PermissionService permissionService;

    @Inject
    ResidentService residentService;

    NationFacade() {
    }

    public void createNation(String nationName, Town capital) throws TownsCommandException {
        if (nationName.length() > config.MAX_NATION_NAME_LENGTH) {
            throw new TownsCommandException("Your name is longer than the maximum (", config.MAX_NATION_NAME_LENGTH, ").");
        }

        if (nationService.getNationFromName(nationName).isPresent()) {
            throw new TownsCommandException("A nation with that name already exists.");
        }

        nationService.createNation(nationName, capital);
        townsMsg.broadcastInfo("The nation of ", GOLD, nationName, DARK_GREEN, " was created.");
    }

    public void setNationName(Player source, String nationName) throws TownsCommandException {
        Nation nation = getPlayerNation(source);

        permissionFacade.checkPermitted(source, nation, NationPermissions.SET_NAME, "change the nation's name");

        nationService.setNationName(nation, nationName);
        townsMsg.info(source, "Nation name set.");
    }

    public void setNationDescription(Player source, Text nationDescription) throws TownsCommandException {
        Nation nation = getPlayerNation(source);

        permissionFacade.checkPermitted(source, nation, NationPermissions.SET_DESCRIPTION, "change the nation's description");

        nationService.setNationDescription(nation, nationDescription);
        townsMsg.info(source, "Nation description set.");
    }

    public void setNationCapital(Player source, Town town) throws TownsCommandException {
        Nation nation = getPlayerNation(source);

        permissionFacade.checkPermitted(source, nation, NationPermissions.SET_CAPITAL, "change the nation's capital");

        // If the town doesn't have a nation, or the town's nation isn't the nation
        if (town.getNation() == null || town.getNation() != nation) {
            throw new TownsCommandException("Town ", town.getName(), " is not part of your nation.");
        }

        nationService.addTown(nation, town);
        nationService.setCapital(nation, town);
        townsMsg.info(source, "Nation capital set.");
    }

    public void addNationAlly(Player source, Nation ally) throws TownsCommandException {
        Nation nation = getPlayerNation(source);

        permissionFacade.checkPermitted(source, nation, NationPermissions.ADD_ALLY, "add allies.");

        if (nation.equals(ally)) {
            throw new TownsCommandException("Cannot add your own nation as an ally.");
        }
        nationService.addNationAlly(nation, ally);
    }

    public void addNationNeutral(Player source, Nation neutral) throws TownsCommandException {
        Nation nation = getPlayerNation(source);

        permissionFacade.checkPermitted(source, nation, NationPermissions.ADD_NEUTRAL, "add neutral nations.");

        nationService.addNationNeutral(nation, neutral);
        townsMsg.info(source, "Your nation is now neutral with ", GOLD, neutral.getName(), DARK_GREEN, ".");
    }

    public void addNationEnemy(Player source, Nation enemy) throws TownsCommandException {
        Nation nation = getPlayerNation(source);

        permissionFacade.checkPermitted(source, nation, NationPermissions.ADD_ENEMY, "add enemies.");

        nationService.addNationEnemy(nation, enemy);
        townsMsg.info(source, "Your nation is now enemies with ", GOLD, DARK_GREEN, ".");
    }

    public void addNationPermission(Player source, User target, NationPermission permission) throws TownsCommandException {
        Nation nation = getPlayerNation(source);

        permissionFacade.checkPermitted(source, nation, NationPermissions.ADD_PERMISSION,
                "grant permissions for your nation.");

        permissionService.permit(residentService.getOrCreate(target), nation, permission);

        townsMsg.info(source, "Gave ", GOLD, target.getName(), " permission ", GOLD, permission.getId(), ".");
        target.getPlayer().ifPresent(player -> {
            townsMsg.info(player, "You were given the permission ", GOLD, permission.getId(), ".");
        });
    }

    public void removeNationPermission(Player source, User target, NationPermission permission) throws TownsCommandException {
        Nation nation = getPlayerNation(source);
        permissionFacade.checkPermitted(source, nation, NationPermissions.ADD_PERMISSION,
                "revoke permissions for your nation.");

        permissionService.remove(residentService.getOrCreate(target), nation, permission, true);

        townsMsg.info(source, "Removed permission ", GOLD, permission.getId(), DARK_GREEN, " from ", GOLD, target.getName(), DARK_GREEN, ".");
        target.getPlayer().ifPresent(player -> {
            townsMsg.info(player, "The permission ", GOLD, permission.getId(), DARK_GREEN, " was taken from you.");
        });
    }

    public void depositToNation(Player source, BigDecimal amount) throws TownsCommandException {
        checkEconomyEnabled();

        Nation nation = getPlayerNation(source);
        permissionFacade.checkPermitted(source, nation, NationPermissions.DEPOSIT_INTO_BANK, "deposit to your nation.");

         Optional<TransferResult> result = Economy.transferCurrency(
                source.getUniqueId(),
                nation.getBank().toString(),
                config.CURRENCY,
                amount,
                Sponge.getCauseStackManager().getCurrentCause()
        );

        if (result.isPresent()) {
            Text feedback = getResultFeedback(
                    result.get().getResult(),
                    Text.of("Deposited ", GOLD, config.CURRENCY.format(amount), DARK_GREEN, " to the nation."),
                    Text.of("You do not have enough to deposit."),
                    Text.of("Depositing failed.")
            );
            townsMsg.info(source, feedback);
        }
    }

    public void withdrawFromNation(Player source, BigDecimal amount) throws TownsCommandException {
        checkEconomyEnabled();

        Nation nation = getPlayerNation(source);
        permissionFacade.checkPermitted(source, nation, NationPermissions.WITHDRAW_FROM_BANK,
                "withdraw from your nation");

         Optional<TransferResult> result = Economy.transferCurrency(
                nation.getBank().toString(),
                source.getUniqueId(),
                config.CURRENCY,
                amount,
                Sponge.getCauseStackManager().getCurrentCause()
        );

        if (result.isPresent()) {
            Text feedback = getResultFeedback(
                    result.get().getResult(),
                    Text.of("Withdrew ", GOLD, config.CURRENCY.format(amount), DARK_GREEN, " from the nation."),
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

        nationInfo
                .append(Text.of(DARK_GREEN, "Capital: ", GOLD, townFacade.renderTown(nation.getCapital()), Text.NEW_LINE))
                .append(Text.of(DARK_GREEN, "Leader: ", GOLD, residentFacade.renderResident(nation.getLeader())), Text.NEW_LINE)
                .append(townsMsg.renderBank(nation.getBank().toString()), Text.NEW_LINE)
                .append(Text.of(DARK_GREEN, "Population: ", GOLD, nationService.getNationPopulation(nation)), Text.NEW_LINE)
                .append(Text.of(
                        DARK_GREEN, "Towns [", GREEN, nation.getTowns().size(), DARK_GREEN, "]: ",
                        GOLD, townFacade.renderTowns(nation.getTowns())
                ));

        if (!nation.getAllies().isEmpty()) {
            nationInfo.append(Text.of(DARK_GREEN, "Allies: ", renderNations(nation.getAllies())), Text.NEW_LINE);
        }

        if (!nation.getEnemies().isEmpty()) {
            nationInfo.append(Text.of(DARK_GREEN, "Enemies: ", renderNations(nation.getEnemies())), Text.NEW_LINE);
        }

        receiver.sendMessage(nationInfo.build());
    }

    public Text renderNation(Nation nation) {
        if (nation == null) {
            return Text.of(GOLD, "No Nation");
        }

        return Text.builder()
                .append(Text.of(GOLD, nation.getName()))
                .onHover(TextActions.showText(Text.of(
                        GOLD, nation.getName(), Text.NEW_LINE,
                        DARK_GREEN, "Leader: ", GOLD, nation.getLeader().getName(), Text.NEW_LINE,
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
        for (Nation nation: nations) {
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

    public Nation getPlayerNation(Player player) throws TownsCommandException {
        Town town = townFacade.getPlayerTown(player);
        Nation nation = town.getNation();

        if (nation == null) {
            throw new TownsCommandException("Your town is not part of a nation!");
        }

        return nation;
    }
}
