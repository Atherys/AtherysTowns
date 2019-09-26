package com.atherys.towns.facade;

import com.atherys.core.economy.Economy;
import com.atherys.towns.TownsConfig;
import com.atherys.towns.api.command.exception.TownsCommandException;
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
import org.spongepowered.api.text.channel.MessageReceiver;

import java.math.BigDecimal;
import java.util.Optional;

import static org.spongepowered.api.text.format.TextColors.DARK_GREEN;
import static org.spongepowered.api.text.format.TextColors.GOLD;

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
    PermissionFacade permissionFacade;

    @Inject
    PermissionService permissionService;

    @Inject
    ResidentService residentService;

    NationFacade() {
    }

    public void createNation(String nationName, String capitalName) throws TownsCommandException {
        Town town = townFacade.getTownFromName(capitalName);

        nationService.createNation(Text.of(nationName), town);
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

    public void setNationCapital(Player source, String townName) throws TownsCommandException {
        Nation nation = getPlayerNation(source);
        Town town = townFacade.getTownFromName(townName);
        permissionFacade.checkPermitted(source, nation, NationPermissions.SET_CAPITAL, "change the nation's capital");

        // If the town doesn't have a nation, or the town's nation isn't the nation
        if (town.getNation() == null || town.getNation() != nation) {
            throw new TownsCommandException("Town ", townName, " is not part of your nation.");
        }

        nationService.addTown(nation, town);
        nationService.setCapital(nation, town);
        townsMsg.info(source, "Nation capital set.");
    }

    public void addNationAlly(Player source, String nationName) throws TownsCommandException {
        Nation nation = getPlayerNation(source);
        permissionFacade.checkPermitted(source, nation, NationPermissions.ADD_ALLY, "add allies.");

        Nation ally = getNationFromName(nationName);
        if (nation.equals(ally)) {
            throw new TownsCommandException("Cannot add your own nation as an ally.");
        }
        nationService.addNationAlly(nation, ally);
    }

    public void addNationNeutral(Player source, String nationName) throws TownsCommandException {
        Nation nation = getPlayerNation(source);
        permissionFacade.checkPermitted(source, nation, NationPermissions.ADD_NEUTRAL, "add neutral nations.");

        Nation neutral = getNationFromName(nationName);
        nationService.addNationNeutral(nation, neutral);
        townsMsg.info(source, "Your nation is now neutral with ", GOLD, nationName, DARK_GREEN, ".");
    }

    public void addNationEnemy(Player source, String nationName) throws TownsCommandException {
        Nation nation = getPlayerNation(source);
        permissionFacade.checkPermitted(source, nation, NationPermissions.ADD_ENEMY, "add enemies.");

        Nation enemy = getNationFromName(nationName);
        nationService.addNationEnemy(nation, enemy);
        townsMsg.info(source, "Your nation is now enemies with ", GOLD, nationName, DARK_GREEN, ".");
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
        Text.Builder info = Text.builder();
        info.append(Text.of("Name: "), nation.getName(), Text.NEW_LINE)
                .append(Text.of("Description: "), nation.getDescription(), Text.NEW_LINE)
                .append(Text.of("Capital: "), nation.getCapital().getName(), Text.NEW_LINE)
                .append(Text.of("Leader: ", nation.getLeader().getName()), Text.NEW_LINE)
                .append(Text.of("Allies: "));

        nation.getAllies().forEach(n -> info.append(n.getName(), Text.of(", ")));
        info.append(Text.NEW_LINE);
        info.append(Text.of("Enemies: "));
        nation.getEnemies().forEach(n -> info.append(n.getName(), Text.of(", ")));
        info.append(Text.NEW_LINE);

        receiver.sendMessage(info.build());
    }

    public void sendNationInfo(MessageReceiver receiver, String nationName) throws TownsCommandException {
        sendNationInfo(receiver, getNationFromName(nationName));
    }

    public void listNations(MessageReceiver receiver) {
        Text.Builder nations = Text.builder().append(Text.of("Nations: "));
        nationService.getAllNations().forEach(nation -> {
            nations.append(Text.of(nation.getName(), "," ));
        });

        receiver.sendMessage(nations.build());
    }

    public Nation getNationFromName(String nationName) throws TownsCommandException {
        return nationService.getNationFromName(nationName).orElseThrow(() -> {
            return TownsCommandException.nationNotFound(nationName);
        });
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
