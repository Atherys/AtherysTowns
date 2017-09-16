package com.atherys.towns.utils;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.user.UserStorageService;

import java.util.Optional;
import java.util.UUID;

public class UserUtils {

    private static UserStorageService userStorage;

    static {
        userStorage = Sponge.getServiceManager().provide(UserStorageService.class).get();
    }

    /**
     *
     * @param uuid the UUID of the player
     * @return An offline User object, or an online Player object. If neither is available, returns empty Optional.
     */
    public static Optional<? extends User> getUser(UUID uuid) {
        Optional<Player> onlinePlayer = Sponge.getServer().getPlayer(uuid);

        if ( onlinePlayer.isPresent( )) {
            return onlinePlayer;
        }
        return userStorage.get(uuid);
    }

}
