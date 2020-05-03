package com.atherys.towns.chat;

import com.atherys.towns.AtherysTowns;
import com.atherys.towns.config.NationConfig;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageReceiver;
import org.spongepowered.api.text.chat.ChatType;
import org.spongepowered.api.text.format.TextColors;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class NationMessageChannel extends ResidentMessageChannel {

    public static final Text NATION_CHAT_PREFIX = Text.of(TextColors.GOLD, "[", TextColors.YELLOW, "NC", TextColors.GOLD, "] ");

    private NationConfig nation;

    public NationMessageChannel(NationConfig nation) {
        this.nation = nation;
    }

    @Override
    public Optional<Text> transformMessage(@Nullable Object sender, MessageReceiver recipient, Text original, ChatType type) {
        return Optional.of(
                Text.of(NATION_CHAT_PREFIX, TextColors.YELLOW, sender == null ? "" : sender, TextColors.RESET, original)
        );
    }

    @Override
    public Collection<MessageReceiver> getMembers() {
        Set<MessageReceiver> onlineResidents = new HashSet<>();

        Sponge.getServer().getOnlinePlayers().forEach(player -> {
            if (AtherysTowns.getInstance().getResidentFacade().isPlayerInNation(player, nation)) {
                onlineResidents.add(player);
            }
        });

        return onlineResidents;
    }
}
