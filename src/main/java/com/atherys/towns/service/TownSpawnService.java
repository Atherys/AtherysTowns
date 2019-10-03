package com.atherys.towns.service;

import com.atherys.towns.TownsConfig;
import com.atherys.towns.entity.Town;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.spongepowered.api.entity.living.player.Player;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Singleton
public class TownSpawnService {
    @Inject
    private TownsConfig config;

    @Inject
    private ResidentService residentService;

    public void spawnPlayer(Player source, Town town) {
        source.setTransformSafely(town.getSpawn());
        if (config.TOWN_COOLDOWN > 0) {
            residentService.setLastTownSpawn(residentService.getOrCreate(source), LocalDateTime.now());
        }
    }

    public Duration cooldownLeft(Player source) {
        LocalDateTime lastSpawn = residentService.getOrCreate(source).getLastTownSpawn();
        return Duration.between(lastSpawn.plus(config.TOWN_COOLDOWN, ChronoUnit.MINUTES), LocalDateTime.now());
    }
}
