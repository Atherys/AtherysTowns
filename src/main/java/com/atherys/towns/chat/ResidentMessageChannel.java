package com.atherys.towns.chat;

import org.spongepowered.api.text.channel.MessageReceiver;
import org.spongepowered.api.text.channel.MutableMessageChannel;

public abstract class ResidentMessageChannel implements MutableMessageChannel {

    public ResidentMessageChannel() {
    }

    @Override
    public boolean addMember(MessageReceiver member) {
        return false;
    }

    @Override
    public boolean removeMember(MessageReceiver member) {
        return false;
    }

    @Override
    public void clearMembers() {
    }
}
