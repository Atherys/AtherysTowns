package com.atherys.towns.facade;

import com.atherys.core.economy.Economy;
import com.atherys.towns.AtherysTowns;
import com.atherys.towns.TownsConfig;
import com.atherys.towns.api.command.TownsCommandException;
import com.atherys.towns.config.RaidConfig;
import com.atherys.towns.model.RaidPoint;
import com.atherys.towns.model.entity.Plot;
import com.atherys.towns.model.entity.Resident;
import com.atherys.towns.model.entity.Town;
import com.atherys.towns.service.TownService;
import com.flowpowered.math.vector.Vector3d;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.bytebuddy.asm.Advice;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.TemporalUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.spongepowered.api.text.format.TextColors.*;

@Singleton
public class TownRaidFacade {

    @Inject
    TownsConfig config;

    @Inject
    TownFacade townFacade;

    @Inject
    TownsMessagingFacade townsMsg;

    Map<Town, RaidPoint> activeRaids = new HashMap<>();

    public TownRaidFacade(){

    }

    public void updateRaidHealth(Town town, int damage) {
        RaidPoint point = activeRaids.get(town);
        point.setHealth(point.getHealth() - damage);
    }

    public boolean hasCooldownPeriodPassed(Town town) {
        if(town.getLastRaidCreationDate() != null) {
            return Duration.between(town.getLastRaidCreationDate(), LocalDateTime.now()).compareTo(config.RAID.RAID_COOLDOWN) >= 0;
        }
        return true;
    }

    public void validateRaid(Player player, Town town) throws TownsCommandException {
        Location<World> location = player.getLocation();
        RaidConfig raidConfig = config.RAID;

        if(AtherysTowns.economyIsEnabled()) {
            Optional<Account> townBank = Economy.getAccount(town.getBank().toString());
            if(townBank.isPresent() && townBank.get().getBalance(config.DEFAULT_CURRENCY).doubleValue() < raidConfig.RAID_COST) {
                throw new TownsCommandException("Your town bank does not have enough money to create a raid point!");
            }
        }

        if(activeRaids.containsKey(town)) {
            throw new TownsCommandException("Your town already has an active raid point!");
        }

        if(activeRaids.values().stream().anyMatch(raidPoint -> raidPoint.getPointLocation() == location)) {
            throw new TownsCommandException("This location is already occupied by another town's raid point!");
        }

        if(!hasCooldownPeriodPassed(town)) {
            throw new TownsCommandException("Raid Cooldown is still in effect!");
        }

    }

    public Location<World> getRaidPointLocation(Player player) {
        return player.getLocation().asHighestLocation();
    }

    public void spawnRaidPoint(Location<World> location, Player player) {
        Vector3d pointLocation = new Vector3d(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        Entity entity = player.getWorld().createEntity(EntityTypes.ARMOR_STAND, pointLocation);
        player.getWorld().spawnEntity(entity);
    }

    public void createRaidPoint(Player player) throws TownsCommandException {
        Town town = townFacade.getPlayerTown(player);
        validateRaid(player, town);

        RaidPoint point = new RaidPoint(LocalDateTime.now(), getRaidPointLocation(player), config.RAID.RAID_HEALTH);
        activeRaids.put(town, point);
        spawnRaidPoint(point.getPointLocation(), player);
        townFacade.getOnlineTownMembers(town).forEach(member -> townsMsg.info(member, "A town raid point has been spawned!"));
    }

    private Text formatRaidLocation(Location<World> location) {
        return Text.of(GOLD, "x: ", location.getBlockX(), ", y: ", location.getBlockY(), ", z: ", location.getBlockZ());
    }

    private Text formatDurationLeft(Town town, LocalDateTime time, Duration duration) {
        Duration durationBetweenPresent = Duration.between(time, LocalDateTime.now());
        Duration durationLeft = duration.minus(durationBetweenPresent);
        return Text.of(GOLD, durationLeft.toMinutes());
    }

    public void sendRaidPointInfo(Player player) throws TownsCommandException {
        Town town = townFacade.getPlayerTown(player);
        boolean raidExists = activeRaids.containsKey(town);
        Text status = raidExists ? Text.of(GREEN, "True") : Text.of(RED, "False");
        Text pointLocation = raidExists ? formatRaidLocation(activeRaids.get(town).getPointLocation()) : Text.of(RED, "No Raid in Progress");
        Text pointHealth = raidExists ? Text.of(GOLD, activeRaids.get(town).getHealth()) : Text.of(RED, "No Raid in Progress");
        Text durationLeft = raidExists ? formatDurationLeft(town, activeRaids.get(town).getCreationTime(), config.RAID.RAID_DURATION) : Text.of(RED, "No Raid in Progress");
        Text cooldown = hasCooldownPeriodPassed(town) ? Text.of(GREEN, "No Cooldown Remaining") : formatDurationLeft(town, town.getLastRaidCreationDate(), config.RAID.RAID_COOLDOWN);

        Text.Builder raidText = Text.builder();

        raidText
                .append(townsMsg.createTownsHeader("Raid Point"));

        raidText
                .append(Text.of(DARK_GREEN, "Raid in Progress: ", status, Text.NEW_LINE))
                .append(Text.of(DARK_GREEN, "Raid Location: ", pointLocation, Text.NEW_LINE))
                .append(Text.of(DARK_GREEN, "Point Health: ", pointHealth, Text.NEW_LINE))
                .append(Text.of(DARK_GREEN, "Duration Left: ", durationLeft, Text.NEW_LINE))
                .append(Text.of(DARK_GREEN, "Cooldown: ", cooldown));

        player.sendMessage(raidText.build());
    }


}
