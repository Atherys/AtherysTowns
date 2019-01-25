package com.atherys.towns.api.chat;

import com.atherys.towns.chat.NationMessageChannel;
import com.atherys.towns.chat.TownMessageChannel;
import com.atherys.towns.entity.Nation;
import com.atherys.towns.entity.Resident;
import com.atherys.towns.entity.Town;
import org.spongepowered.api.text.Text;

public interface TownsChatService {

    TownMessageChannel getChannel(Town town);

    NationMessageChannel getChannel(Nation nation);

    void broadcast(Town town, Object... message);

    void broadcast(Town town, Text text);

    void broadcast(Nation nation, Object... message);

    void broadcast(Nation nation, Text text);

}
