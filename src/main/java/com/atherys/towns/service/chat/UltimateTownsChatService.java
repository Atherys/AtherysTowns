package com.atherys.towns.service.chat;

import com.atherys.towns.api.chat.TownsChatService;
import com.atherys.towns.chat.NationMessageChannel;
import com.atherys.towns.chat.TownMessageChannel;
import com.atherys.towns.entity.Nation;
import com.atherys.towns.entity.Resident;
import com.atherys.towns.entity.Town;
import com.google.inject.Singleton;
import org.spongepowered.api.text.Text;

@Singleton
public class UltimateTownsChatService implements TownsChatService {
    @Override
    public TownMessageChannel createChannel(Town town) {
        return null;
    }

    @Override
    public NationMessageChannel createChannel(Nation nation) {
        return null;
    }

    @Override
    public TownMessageChannel getChannel(Town town) {
        return null;
    }

    @Override
    public NationMessageChannel getChannel(Nation nation) {
        return null;
    }

    @Override
    public void addToChannel(Town town, Resident resident) {

    }

    @Override
    public void addToChannel(Nation nation, Resident resident) {

    }

    @Override
    public void removeFromChannel(Town town, Resident resident) {

    }

    @Override
    public void removeFromChannel(Nation nation, Resident resident) {

    }

    @Override
    public void broadcast(Town town, Object... message) {

    }

    @Override
    public void broadcast(Town town, Text text) {

    }

    @Override
    public void broadcast(Nation nation, Object... message) {

    }

    @Override
    public void broadcast(Nation nation, Text text) {

    }
}
