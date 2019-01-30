package com.atherys.towns.facade;

import com.google.inject.Singleton;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

@Singleton
public class TownsMessagingFacade {

    public static final Text PREFIX = Text.of(TextColors.DARK_AQUA, "[", TextColors.DARK_GREEN, "Towns", TextColors.DARK_AQUA, "] ");

    public Text formatInfo(Object... message) {
        return Text.of(PREFIX, TextColors.AQUA, Text.of(message));
    }

    public Text formatError(Object... message) {
        return Text.of(PREFIX, TextColors.RED, Text.of(message));
    }

    public void info(Player player, Object... message) {
        player.sendMessage(formatInfo(message));
    }

    public void error(Player player, Object... message) {
        player.sendMessage(formatError(message));
    }

    public void broadcastInfo(Object... message) {
        Sponge.getServer().getBroadcastChannel().send(formatInfo(message));
    }

    public void broadcastError(Object... message) {
        Sponge.getServer().getBroadcastChannel().send(formatError(message));
    }
}
