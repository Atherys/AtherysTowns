package com.atherys.towns.messaging;

import com.atherys.towns.AtherysTowns;
import com.atherys.towns.TownsConfig;
import com.atherys.towns.town.Town;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.chat.ChatTypes;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.text.title.Title;

public class TownMessage {

    public static final Text MSG_PREFIX = Text.of( TownsConfig.PRIMARY_COLOR, "[", TownsConfig.SECONDARY_COLOR, "Towns", TownsConfig.PRIMARY_COLOR, "] ", TownsConfig.PRIMARY_COLOR );
    public static final Text TOWN_CHAT_PREFIX = Text.of( TownsConfig.TERTIARY_COLOR, "<TC> " );

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
        player.sendMessage(ChatTypes.CHAT, Text.builder().append(MSG_PREFIX).append(Text.of( TownsConfig.TERTIARY_COLOR, text )).build());
    }

    public static void inform ( Player player, Object... text ) {
        inform(player, Text.of(text));
    }

    public static void warn ( Player player, Text text ) {
        player.sendMessage(ChatTypes.CHAT, Text.builder().append(MSG_PREFIX).append(Text.of( TownsConfig.WARNING_COLOR, text )).build());
    }

    public static void warn ( Player player, Object... text ) {
        warn(player, Text.of(text));
    }

    public static void titleAnnounce ( Player player, Text text ) {
        player.sendTitle(
            Title.builder()
            .fadeIn(TownsConfig.TITLE_FADEIN_TICKS)
            .stay(TownsConfig.TITLE_STAY_TICKS)
            .fadeOut(TownsConfig.TITLE_FADEOUT_TICKS)
            .title(text)
            .build()
        );
    }

    public static void subtitleAnnounce ( Player player, Text text ) {
        player.sendTitle(
                Title.builder()
                        .fadeIn(TownsConfig.SUBTITLE_FADEIN_TICKS)
                        .stay(TownsConfig.SUBTITLE_STAY_TICKS)
                        .fadeOut(TownsConfig.SUBTITLE_FADEOUT_TICKS)
                        .subtitle(text)
                        .build()
        );
    }

    public static void announce ( Player player, Text title, Text subtitle ) {
        titleAnnounce(player, title);
        subtitleAnnounce(player, subtitle);
    }

    public static void enterTown ( Player player, Town town ) {
        subtitleAnnounce( player, Text.of ( TownsConfig.TERTIARY_COLOR, "You have entered ", TextStyles.BOLD, town.getColor(), town.getName() ) );
    }

    public static void leaveTown ( Player player, Town town ) {
        subtitleAnnounce( player, Text.of ( TownsConfig.TERTIARY_COLOR, "You have left ", TextStyles.BOLD, town.getColor(), town.getName() ) );
    }

}
