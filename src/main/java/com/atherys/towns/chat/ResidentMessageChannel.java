package com.atherys.towns.chat;

import com.atherys.towns.entity.Resident;
import org.spongepowered.api.text.channel.MessageReceiver;
import org.spongepowered.api.text.channel.MutableMessageChannel;

import java.util.Collection;

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
