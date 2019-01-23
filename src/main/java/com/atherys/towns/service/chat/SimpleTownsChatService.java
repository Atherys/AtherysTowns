package com.atherys.towns.service.chat;

import com.atherys.towns.api.chat.TownsChatService;
import com.atherys.towns.chat.NationMessageChannel;
import com.atherys.towns.chat.TownMessageChannel;
import com.atherys.towns.entity.Nation;
import com.atherys.towns.entity.Resident;
import com.atherys.towns.entity.Town;
import com.google.inject.Singleton;
import org.spongepowered.api.text.Text;

import java.util.HashMap;
import java.util.UUID;

@Singleton
public class SimpleTownsChatService implements TownsChatService {

    private HashMap<UUID, NationMessageChannel> nationChannels = new HashMap<>();

    private HashMap<UUID, TownMessageChannel> townChannels = new HashMap<>();

    @Override
    public TownMessageChannel createChannel(Town town) {
        TownMessageChannel townMessageChannel = new TownMessageChannel(town);
        townChannels.put(town.getId(), townMessageChannel);
        return townMessageChannel;
    }

    @Override
    public NationMessageChannel createChannel(Nation nation) {
        NationMessageChannel nationMessageChannel = new NationMessageChannel(nation);
        nationChannels.put(nation.getId(), nationMessageChannel);
        return nationMessageChannel;
    }

    @Override
    public TownMessageChannel getChannel(Town town) {
        return townChannels.get(town.getId());
    }

    @Override
    public NationMessageChannel getChannel(Nation nation) {
        return nationChannels.get(nation.getId());
    }

    @Override
    public void addToChannel(Town town, Resident resident) {
        getOrCreateChannel(town).addResident(resident);
    }

    @Override
    public void addToChannel(Nation nation, Resident resident) {
        getOrCreateChannel(nation).addResident(resident);
    }

    @Override
    public void removeFromChannel(Town town, Resident resident) {
        getOrCreateChannel(town).removeResident(resident);
    }

    @Override
    public void removeFromChannel(Nation nation, Resident resident) {
        getOrCreateChannel(nation).removeResident(resident);
    }

    @Override
    public void broadcast(Town town, Object... message) {
        getOrCreateChannel(town).send(Text.of(message));
    }

    @Override
    public void broadcast(Town town, Text text) {
        getOrCreateChannel(town).send(text);
    }

    @Override
    public void broadcast(Nation nation, Object... message) {
        getOrCreateChannel(nation).send(Text.of(message));
    }

    @Override
    public void broadcast(Nation nation, Text text) {
        getOrCreateChannel(nation).send(text);
    }
}
