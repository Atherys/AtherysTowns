package com.atherys.towns.util;

import com.atherys.core.utils.UserUtils;
import com.atherys.towns.api.command.exception.TownsCommandException;
import org.spongepowered.api.entity.living.player.User;

public class CommandUtils {
    public static User getUser(String name) throws TownsCommandException {
        return UserUtils.getUser(name).orElseThrow(() -> TownsCommandException.playerNotFound(name));
    }
}
