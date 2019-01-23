package com.atherys.towns.api.chat;

import com.atherys.towns.chat.NationMessageChannel;
import com.atherys.towns.chat.TownMessageChannel;
import com.atherys.towns.entity.Nation;
import com.atherys.towns.entity.Resident;
import com.atherys.towns.entity.Town;
import org.spongepowered.api.text.Text;

public interface TownsChatService {

    default TownMessageChannel getOrCreateChannel(Town town) {
        TownMessageChannel channel = getChannel(town);

        if ( channel == null ) {
            return createChannel(town);
        }

        return channel;
    }

    default NationMessageChannel getOrCreateChannel(Nation nation) {
        NationMessageChannel channel = getChannel(nation);

        if ( channel == null ) {
            return createChannel(nation);
        }

        return channel;
    }

    TownMessageChannel createChannel(Town town);

    NationMessageChannel createChannel(Nation nation);

    TownMessageChannel getChannel(Town town);

    NationMessageChannel getChannel(Nation nation);

    void addToChannel(Town town, Resident resident);

    void addToChannel(Nation nation, Resident resident);

    void removeFromChannel(Town town, Resident resident);

    void removeFromChannel(Nation nation, Resident resident);

    void broadcast(Town town, Object... message);

    void broadcast(Town town, Text text);

    void broadcast(Nation nation, Object... message);

    void broadcast(Nation nation, Text text);

}
