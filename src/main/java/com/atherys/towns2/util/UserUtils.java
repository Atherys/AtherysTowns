package com.atherys.towns2.util;

import java.util.Optional;
import java.util.UUID;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.user.UserStorageService;

public class UserUtils {

    private static UserStorageService userStorageService;

    public static Optional<User> getUser(UUID uuid) {
        if ( userStorageService == null ) userStorageService = Sponge.getServiceManager().provide(UserStorageService.class).get();
        for ( Player player : Sponge.getServer().getOnlinePlayers() ) if ( player.getUniqueId().equals(uuid) ) return Optional.of(player);
        return userStorageService.get(uuid);
    }

}
