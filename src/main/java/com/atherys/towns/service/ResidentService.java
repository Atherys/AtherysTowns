package com.atherys.towns.service;

import com.atherys.towns.entity.Resident;
import com.atherys.towns.persistence.ResidentRepository;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.user.UserStorageService;

import java.lang.ref.WeakReference;
import java.util.Optional;
import java.util.UUID;

@Singleton
public class ResidentService {

    @Inject
    ResidentRepository residentRepository;

    @Inject
    UserStorageService userStorageService;

    ResidentService() {
    }

    protected Resident getOrCreate(UUID playerUuid, String playerName) {
        Optional<Resident> resident = residentRepository.findById(playerUuid);

        if (resident.isPresent()) {
            return resident.get();
        } else {
            Resident newResident = new Resident();

            newResident.setId(playerUuid);
            newResident.setName(playerName);

            residentRepository.saveOne(newResident);

            return newResident;
        }
    }

    public Resident getOrCreate(User src) {
        return getOrCreate(src.getUniqueId(), src.getName());
    }

    public Optional<User> getUserFromResident(Resident resident) {
        return userStorageService.get(resident.getUniqueId());
    }

    public Optional<Player> getPlayerFromResident(Resident resident) {

        for (Player onlinePlayer : Sponge.getServer().getOnlinePlayers()) {
            if ( onlinePlayer.getUniqueId().equals(resident.getId()) ) {
                return Optional.of(onlinePlayer);
            }
        }

        Optional<User> user = getUserFromResident(resident);
        return user.flatMap(User::getPlayer);
    }
}
