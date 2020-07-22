package com.atherys.towns.facade;

import com.atherys.towns.AtherysTowns;
import com.atherys.towns.TownsConfig;
import com.atherys.towns.api.command.TownsCommandException;
import com.atherys.towns.model.entity.Resident;
import com.atherys.towns.model.entity.Town;
import com.atherys.towns.service.ResidentService;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scheduler.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.spongepowered.api.text.format.TextColors.DARK_GREEN;
import static org.spongepowered.api.text.format.TextColors.GOLD;

@Singleton
public class TownSpawnFacade {
    @Inject
    private TownsConfig config;

    @Inject
    private ResidentService residentService;

    @Inject
    private TownFacade townFacade;

    @Inject
    private TownsMessagingFacade townsMsg;

    private final Task task = Task.builder()
            .execute(() -> {
                if (config.TOWN.TOWN_WARMUP == 0) return;

                Map<Player, Resident> residents = Sponge.getServer().getOnlinePlayers().stream()
                        .collect(Collectors.toMap(p -> p, residentService::getOrCreate));

                residents.forEach((player, resident) -> {
                    if (resident.getWarmupSecondsLeft() > 0) {
                        resident.setWarmupSecondsLeft(resident.getWarmupSecondsLeft() - 1);
                        if (resident.getWarmupSecondsLeft() == 0) {
                            teleport(player, resident);
                        }
                    }
                });
            })
            .interval(1, TimeUnit.SECONDS)
            .name("atherystowns-warmups")
            .submit(AtherysTowns.getInstance());

    /**
     * Teleports a player to their town spawn.
     * <p>
     * First, check if they have any cooldown left.
     * <p>
     * Second, check if there is any warmup time. If there is none, we don't need to bother setting it. If there
     * is, set the warmup time.
     * <p>
     * Third, teleport the player. If there is no cooldown, don't set their last town spawn.
     */
    public void spawnPlayerTown(Player source) throws TownsCommandException {
        Town town = townFacade.getPlayerTown(source);
        Resident resident = residentService.getOrCreate(source);
        Duration timeLeft = Duration.between(
                resident.getLastTownSpawn().plus(config.TOWN.TOWN_COOLDOWN, ChronoUnit.MINUTES),
                LocalDateTime.now()
        );

        if (timeLeft.isNegative()) {
            long minutes = Math.round(timeLeft.abs().getSeconds() / 60.0);
            String unit = minutes == 1 ? " minute" : " minutes";
            throw new TownsCommandException(minutes + unit + " left on cooldown.");
        }

        if (config.TOWN.TOWN_WARMUP > 0) {
            resident.setWarmupSecondsLeft(config.TOWN.TOWN_WARMUP);
            townsMsg.info(source, "Teleporting in ", GOLD, config.TOWN.TOWN_WARMUP, DARK_GREEN, " seconds.");
        } else {
            teleport(source, resident);
        }
    }

    private void teleport(Player source, Resident resident) {
        source.setTransformSafely(resident.getTown().getSpawn());
        if (config.TOWN.TOWN_COOLDOWN > 0) {
            residentService.setLastTownSpawn(resident, LocalDateTime.now());
        }
    }

    public void onPlayerMove(Player source) {
        Resident resident = residentService.getOrCreate(source);

        if (resident.getWarmupSecondsLeft() > 0) {
            townsMsg.error(source, "Teleportation cancelled!");
            resident.setWarmupSecondsLeft(0);
        }
    }
}
