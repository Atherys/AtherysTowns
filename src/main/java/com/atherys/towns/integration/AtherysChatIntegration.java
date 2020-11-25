package com.atherys.towns.integration;

import com.atherys.chat.AtherysChat;
import com.atherys.towns.chat.NationChannel;
import com.atherys.towns.chat.TownChannel;

public final class AtherysChatIntegration {
    public static void registerChannels() {
        AtherysChat.getInstance().getChatService().registerChannel(new TownChannel());
        AtherysChat.getInstance().getChatService().registerChannel(new NationChannel());
    }
}
