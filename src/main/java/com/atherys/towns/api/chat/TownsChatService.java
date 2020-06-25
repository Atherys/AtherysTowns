package com.atherys.towns.api.chat;

import com.atherys.towns.chat.NationMessageChannel;
import com.atherys.towns.chat.TownMessageChannel;
import com.atherys.towns.config.NationConfig;
import com.atherys.towns.model.entity.Town;
import org.spongepowered.api.text.Text;

public interface TownsChatService {

    TownMessageChannel getChannel(Town town);

    NationMessageChannel getChannel(NationConfig nation);

    void broadcast(Town town, Object... message);

    void broadcast(Town town, Text text);

    void broadcast(NationConfig nation, Object... message);

    void broadcast(NationConfig nation, Text text);

}
