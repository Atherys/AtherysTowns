package com.atherys.towns.service;

import com.atherys.towns.entity.Resident;
import com.google.inject.Singleton;
import org.spongepowered.api.entity.living.player.User;

import java.util.UUID;

@Singleton
public class ResidentService {
    ResidentService() {
    }

    protected Resident getOrCreate(UUID playerUuid, String playerName) {
        Resident resident = new Resident();

        resident.setId(playerUuid);
        resident.setName(playerName);

        return resident;
    }

    public Resident getOrCreate(User src) {
        return getOrCreate(src.getUniqueId(), src.getName());
    }
}
