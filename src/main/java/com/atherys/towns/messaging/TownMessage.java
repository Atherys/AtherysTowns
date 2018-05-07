package com.atherys.towns.messaging;

import com.atherys.towns.AtherysTowns;
import com.atherys.towns.town.Town;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.chat.ChatTypes;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.text.title.Title;

public class TownMessage {

    public static final Text MSG_PREFIX = Text
            .of(AtherysTowns.getConfig().COLORS.PRIMARY, "[", AtherysTowns.getConfig().COLORS.SECONDARY,
                    "Towns", AtherysTowns.getConfig().COLORS.PRIMARY, "] ",
                    AtherysTowns.getConfig().COLORS.PRIMARY);

    public static void informAll(Text text) {
        for (Player p : AtherysTowns.getInstance().getGame().getServer().getOnlinePlayers()) {
            inform(p, text);
        }
    }

    public static void warnAll(Text text) {
        for (Player p : AtherysTowns.getInstance().getGame().getServer().getOnlinePlayers()) {
            warn(p, text);
        }
    }

    public static void inform(Player player, Text text) {
        player.sendMessage(ChatTypes.CHAT, Text.builder().append(MSG_PREFIX)
                .append(Text.of(AtherysTowns.getConfig().COLORS.TEXT, text)).build());
    }

    public static void inform(Player player, Object... text) {
        inform(player, Text.of(text));
    }

    public static void warn(Player player, Text text) {
        player.sendMessage(ChatTypes.CHAT, Text.builder().append(MSG_PREFIX)
                .append(Text.of(AtherysTowns.getConfig().COLORS.WARNING, text)).build());
    }

    public static void warn(Player player, Object... text) {
        warn(player, Text.of(text));
    }

    public static void titleAnnounce(Player player, Text text) {
        player.sendTitle(
                Title.builder()
                        .fadeIn(AtherysTowns.getConfig().TITLES.FADE_IN)
                        .stay(AtherysTowns.getConfig().TITLES.STAY)
                        .fadeOut(AtherysTowns.getConfig().TITLES.FADE_OUT)
                        .title(text)
                        .build()
        );
    }

    public static void subtitleAnnounce(Player player, Text text) {
        player.sendTitle(
                Title.builder()
                        .fadeIn(AtherysTowns.getConfig().TITLES.SUB_FADE_IN)
                        .stay(AtherysTowns.getConfig().TITLES.SUB_STAY)
                        .fadeOut(AtherysTowns.getConfig().TITLES.SUB_FADE_OUT)
                        .subtitle(text)
                        .build()
        );
    }

    public static void announce(Player player, Text title, Text subtitle) {
        titleAnnounce(player, title);
        subtitleAnnounce(player, subtitle);
    }

    public static void enterTown(Player player, Town town) {
        subtitleAnnounce(player,
                Text.of(AtherysTowns.getConfig().COLORS.TERTIARY, "You have entered ", TextStyles.BOLD,
                        town.getColor(), town.getName()));
    }

    public static void leaveTown(Player player, Town town) {
        subtitleAnnounce(player,
                Text.of(AtherysTowns.getConfig().COLORS.TERTIARY, "You have left ", TextStyles.BOLD,
                        town.getColor(), town.getName()));
    }

}
