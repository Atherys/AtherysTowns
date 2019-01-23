package com.atherys.towns.chat;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.channel.MessageReceiver;
import org.spongepowered.api.text.channel.MutableMessageChannel;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * A Message channel which stores UUIDs and sends messages to online players,
 * whose UUIDs are part of this message channel.
 */
public class UUIDMessageChannel implements MutableMessageChannel {

    private Set<UUID> members = new HashSet<>();

    @Override
    public boolean addMember(MessageReceiver member) {
        if ( member instanceof Player) {
            return addMember(((Player) member).getUniqueId());
        }

        return false;
    }

    @Override
    public boolean removeMember(MessageReceiver member) {
        if ( member instanceof Player ) {
            return removeMember(((Player) member).getUniqueId());
        }

        return false;
    }

    public boolean addMember(UUID member) {
        return members.add(member);
    }

    public boolean removeMember(UUID member) {
        return members.remove(member);
    }

    @Override
    public void clearMembers() {
        members.clear();
    }

    @Override
    public Collection<MessageReceiver> getMembers() {
        Set<MessageReceiver> players = new HashSet<>();

        Sponge.getServer().getOnlinePlayers().forEach(player -> {
            if (members.contains(player.getUniqueId())) {
                players.add(player);
            }
        });

        return players;
    }

}
