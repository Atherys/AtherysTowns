package com.atherys.towns.facade;

import com.atherys.towns.api.command.exception.TownsCommandException;
import com.atherys.towns.api.permission.nation.NationPermission;
import com.atherys.towns.api.permission.nation.NationPermissions;
import com.atherys.towns.entity.Nation;
import com.atherys.towns.entity.Resident;
import com.atherys.towns.entity.Town;
import com.atherys.towns.service.NationService;
import com.atherys.towns.service.PermissionService;
import com.atherys.towns.service.ResidentService;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageReceiver;

import java.util.Optional;

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

    public void addNationNeutral(Player source, String nationName) throws TownsCommandException {
        Nation nation = getPlayerNation(source);

        if (permissionFacade.isPermitted(source, nation, NationPermissions.ADD_NEUTRAL)) {
            Nation neutral = getNationFromName(nationName);
            nationService.addNationNeutral(nation, neutral);
            townsMsg.info(source, "Your nation is now neutral with ", GOLD, nationName, DARK_GREEN, ".");
        } else {
            throw new TownsCommandException("You are not permitted to add neutral nations.");
        }
    }

    public void addNationEnemy(Player source, String nationName) throws TownsCommandException {
        Nation nation = getPlayerNation(source);

        if (permissionFacade.isPermitted(source, nation, NationPermissions.ADD_ENEMY)) {
            Nation enemy = getNationFromName(nationName);
            nationService.addNationEnemy(nation, enemy);
            townsMsg.info(source, "Your nation is now enemies with ", GOLD, nationName, DARK_GREEN, ".");
        } else {
            throw new TownsCommandException("You are not permitted to add enemy nations.");
        }
    }

    public void addNationPermission(Player source, User user, NationPermission permission) throws TownsCommandException {
        Nation nation = getPlayerNation(source);

        if (!partOfSameNation(source, user)) {
            throw new TownsCommandException(user.getName(), " is not part of your nation.");
        }

        if (permissionFacade.isPermitted(source, nation, NationPermissions.ADD_PERMISSION)) {
            permissionService.permit(residentService.getOrCreate(user), nation, permission);

            townsMsg.info(source, "Gave ", GOLD, user.getName(), " permission ", GOLD, permission.getId(), ".");
            user.getPlayer().ifPresent(player -> {
                townsMsg.info(player, "You were given the permission ", GOLD, permission.getId(), ".");
            });
        } else {
            throw new TownsCommandException("You are not permitted to grant permissions.");
        }
    }

    public void removeNationPermission(Player source, User user, NationPermission permission) throws TownsCommandException {
        Nation nation = getPlayerNation(source);

        if (!partOfSameNation(source, user)) {
            throw new TownsCommandException(user.getName(), " is not part of your nation.");
        }

        if (permissionFacade.isPermitted(source, nation, NationPermissions.ADD_PERMISSION)) {
            permissionService.remove(residentService.getOrCreate(user), nation, permission, true);

            townsMsg.info(source, "Removed permission ", GOLD, permission.getId(), DARK_GREEN, " from ", GOLD, user.getName(), DARK_GREEN, ".");
            user.getPlayer().ifPresent(player -> {
                townsMsg.info(player, "The permission ", GOLD, permission.getId(), DARK_GREEN, " was taken from you.");
            });
        } else {
            throw new TownsCommandException("You are not permitted to grant permissions.");
        }
    }

    public void sendPlayerNationInfo(Player player) throws TownsCommandException {
       sendNationInfo(player, getPlayerNation(player));
    }

    public void sendNationInfo(MessageReceiver receiver, Nation nation) {
        Text.Builder info = Text.builder();
        info.append(Text.of("Name: "), nation.getName(), Text.NEW_LINE);
        info.append(Text.of("Description: "), nation.getDescription(), Text.NEW_LINE);
        info.append(Text.of("Capital: "), nation.getCapital().getName(), Text.NEW_LINE);
        info.append(Text.of("Leader: ", nation.getLeader().getName()), Text.NEW_LINE);
        info.append(Text.of("Allies: "));
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

    public Nation getNationFromName(String nationName) throws TownsCommandException {
        return nationService.getNationFromName(nationName).orElseThrow(() -> {
            return TownsCommandException.nationNotFound(nationName);
        });
    }

    public boolean partOfSameNation(User user, User other) {
        Town town = residentService.getOrCreate(user).getTown();
        Town otherTown = residentService.getOrCreate(other).getTown();

        if (town == null || otherTown == null) return  false;

        return (town.getNation() != null && town.getNation().equals(otherTown.getNation()));
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
