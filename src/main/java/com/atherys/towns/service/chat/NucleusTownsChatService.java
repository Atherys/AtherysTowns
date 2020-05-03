package com.atherys.towns.service.chat;

import com.atherys.towns.api.chat.TownsChatService;
import com.atherys.towns.chat.NationMessageChannel;
import com.atherys.towns.chat.TownMessageChannel;
import com.atherys.towns.config.NationConfig;
import com.atherys.towns.model.entity.Town;
import com.google.inject.Singleton;
import org.spongepowered.api.text.Text;

@Singleton
public class NucleusTownsChatService implements TownsChatService {

    @Override
    public TownMessageChannel getChannel(Town town) {
        return null;
    }

    @Override
    public NationMessageChannel getChannel(NationConfig nation) {
        return null;
    }

    @Override
    public void broadcast(Town town, Object... message) {

    }

    @Override
    public void broadcast(Town town, Text text) {

    }

    @Override
    public void broadcast(NationConfig nation, Object... message) {

    }

    @Override
    public void broadcast(NationConfig nation, Text text) {

    }
}
