package com.atherys.towns.facade;

import com.atherys.towns.api.command.exception.TownsCommandException;
import com.atherys.towns.api.permission.nation.NationPermissions;
import com.atherys.towns.entity.Nation;
import com.atherys.towns.entity.Town;
import com.atherys.towns.service.NationService;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageReceiver;

import static org.spongepowered.api.text.format.TextColors.DARK_GREEN;
import static org.spongepowered.api.text.format.TextColors.GOLD;

@Singleton
public class NationFacade {
    @Inject
    NationService nationService;

    @Inject
    TownsMessagingFacade townsMsg;

    @Inject
    TownFacade townFacade;

    @Inject
    PermissionFacade permissionFacade;

    NationFacade() {
    }

    public void createNation(String nationName, String capitalName) throws TownsCommandException {
        Town town = townFacade.getTownFromName(capitalName);

        nationService.createNation(Text.of(nationName), town);
        townsMsg.broadcastInfo("The nation of ", GOLD, nationName, DARK_GREEN, " was created.");
    }

    public void setNationName(Player player, String nationName) throws TownsCommandException {
        Nation nation = getPlayerNation(player);

        if (permissionFacade.isPermitted(player, nation, NationPermissions.SET_NAME)) {
            nationService.setNationName(nation, nationName);
            townsMsg.info(player, "Nation name set.");
        } else {
            throw TownsCommandException.notPermittedForNation("name");
        }
    }

    public void setNationDescription(Player player, Text nationDescription) throws TownsCommandException {
        Nation nation = getPlayerNation(player);

        if (permissionFacade.isPermitted(player, nation, NationPermissions.SET_DESCRIPTION)) {
            nationService.setNationDescription(nation, nationDescription);
            townsMsg.info(player, "Nation description set.");
        } else {
            throw TownsCommandException.notPermittedForNation("description");
        }
    }

    public void addNationAlly(Player player, String nationName) throws TownsCommandException {
        Nation nation = getPlayerNation(player);
        Nation ally = getNationFromName(nationName);

        if (permissionFacade.isPermitted(player, nation, NationPermissions.ADD_ALLY)) {
            if (nation.equals(ally)) {
                throw new TownsCommandException("Cannot add your nation as an ally.");
            }
            nationService.addNationAlly(nation, ally);
        }
    }

    public void setNationCapital(Player player, String townName) throws TownsCommandException {
        Nation nation = getPlayerNation(player);
        Town town = townFacade.getTownFromName(townName);

        if (permissionFacade.isPermitted(player, nation, NationPermissions.SET_CAPITAL)) {
            // If the town doesn't have a nation, or the town's nation isn't the nation
            if (town.getNation() == null || town.getNation() != nation) {
                throw new TownsCommandException("Town ", townName, " is not part of your nation.");
            }

            nationService.addTown(nation, town);
            nationService.setCapital(nation, town);
            townsMsg.info(player, "Nation capital set.");
        } else {
            throw TownsCommandException.notPermittedForNation("capital");
        }
    }

    public void sendPlayerNationInfo(Player player) throws TownsCommandException {
       sendNationInfo(player, getPlayerNation(player));
    }

    public void sendNationInfo(MessageReceiver receiver, Nation nation) {
        receiver.sendMessage(nation.getName());
    }

    public void sendNationInfo(MessageReceiver receiver, String nationName) throws TownsCommandException {
        sendNationInfo(receiver, getNationFromName(nationName));
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
