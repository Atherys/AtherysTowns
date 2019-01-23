package com.atherys.towns.chat;

import com.atherys.towns.entity.Nation;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageReceiver;
import org.spongepowered.api.text.chat.ChatType;
import org.spongepowered.api.text.format.TextColors;

import javax.annotation.Nullable;
import java.util.Optional;

public class NationMessageChannel extends ResidentMessageChannel {

    public static final Text NATION_CHAT_PREFIX = Text.of(TextColors.GOLD, "[", TextColors.YELLOW, "NC", TextColors.GOLD, "] ");

    public NationMessageChannel(Nation nation) {
        nation.getTowns().forEach(town -> town.getResidents().forEach(this::addResident));
    }

    @Override
    public Optional<Text> transformMessage(@Nullable Object sender, MessageReceiver recipient, Text original, ChatType type) {
        return Optional.of(
                Text.of(NATION_CHAT_PREFIX, TextColors.YELLOW, sender == null ? "" : sender, TextColors.RESET, original)
        );
    }
}
