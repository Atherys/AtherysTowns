package com.atherys.towns.chat;

import com.atherys.towns.AtherysTowns;
import com.atherys.towns.entity.Town;
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

public class TownMessageChannel extends ResidentMessageChannel {

    public static final Text TOWN_CHAT_PREFIX = Text.of(TextColors.DARK_GREEN, "[", TextColors.AQUA, "TC", TextColors.DARK_GREEN, "] ");

    private Town town;

    public TownMessageChannel(Town town) {
        this.town = town;
    }

    @Override
    public Optional<Text> transformMessage(@Nullable Object sender, MessageReceiver recipient, Text original, ChatType type) {
        return Optional.of(
                Text.of(TOWN_CHAT_PREFIX, TextColors.AQUA, sender == null ? "" : sender, TextColors.RESET, original)
        );
    }

    @Override
    public Collection<MessageReceiver> getMembers() {
        Set<MessageReceiver> onlineResidents = new HashSet<>();

        Sponge.getServer().getOnlinePlayers().forEach(player ->  {
            if (AtherysTowns.getInstance().getResidentFacade().isPlayerInTown(player, town)) {
                onlineResidents.add(player);
            }
        });

        return onlineResidents;
    }
}
