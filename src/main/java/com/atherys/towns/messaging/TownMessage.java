package com.atherys.towns.messaging;

import com.atherys.towns.AtherysTowns;
import com.atherys.towns.Settings;
import com.atherys.towns.town.Town;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.chat.ChatTypes;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.text.title.Title;

public class TownMessage {

    public static final Text MSG_PREFIX = Text.of( Settings.PRIMARY_COLOR, "[", Settings.SECONDARY_COLOR, "Towns", Settings.PRIMARY_COLOR, "] ", Settings.PRIMARY_COLOR );
    public static final Text TOWN_CHAT_PREFIX = Text.of( Settings.TERTIARY_COLOR, "<TC> " );

    public static void informAll ( Text text ) {
        for ( Player p : AtherysTowns.getInstance().getGame().getServer().getOnlinePlayers() ) {
            inform(p, text);
        }
    }

    public static void warnAll ( Text text ) {
        for ( Player p : AtherysTowns.getInstance().getGame().getServer().getOnlinePlayers() ) {
            warn(p, text);
        }
    }

    public static void inform ( Player player, Text text ) {
        player.sendMessage(ChatTypes.CHAT, Text.builder().append(MSG_PREFIX).append(Text.of( Settings.TERTIARY_COLOR, text )).build());
    }

    public static void inform ( Player player, Object... text ) {
        inform(player, Text.of(text));
    }

    public static void warn ( Player player, Text text ) {
        player.sendMessage(ChatTypes.CHAT, Text.builder().append(MSG_PREFIX).append(Text.of( Settings.WARNING_COLOR, text )).build());
    }

    public static void warn ( Player player, Object... text ) {
        warn(player, Text.of(text));
    }

    public static void titleAnnounce ( Player player, Text text ) {
        player.sendTitle(
            Title.builder()
            .fadeIn(Settings.TITLE_FADEIN_TICKS)
            .stay(Settings.TITLE_STAY_TICKS)
            .fadeOut(Settings.TITLE_FADEOUT_TICKS)
            .title(text)
            .build()
        );
    }

    public static void subtitleAnnounce ( Player player, Text text ) {
        player.sendTitle(
                Title.builder()
                        .fadeIn(Settings.SUBTITLE_FADEIN_TICKS)
                        .stay(Settings.SUBTITLE_STAY_TICKS)
                        .fadeOut(Settings.SUBTITLE_FADEOUT_TICKS)
                        .subtitle(text)
                        .build()
        );
    }

    public static void announce ( Player player, Text title, Text subtitle ) {
        titleAnnounce(player, title);
        subtitleAnnounce(player, subtitle);
    }

    public static void enterTown ( Player player, Town town ) {
        subtitleAnnounce( player, Text.of ( Settings.TERTIARY_COLOR, "You have entered ", TextStyles.BOLD, town.getColor(), town.getName() ) );
    }

    public static void leaveTown ( Player player, Town town ) {
        subtitleAnnounce( player, Text.of ( Settings.TERTIARY_COLOR, "You have left ", TextStyles.BOLD, town.getColor(), town.getName() ) );
    }

}
