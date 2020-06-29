package com.atherys.towns.api.event;

import com.atherys.towns.model.Vote;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.impl.AbstractEvent;

public class PlayerVoteEvent extends AbstractEvent {

    private final Vote vote;
    private final Player voter;

    public PlayerVoteEvent(Player voter, Vote vote) {
        this.vote = vote;
        this.voter = voter;
    }

    @Override
    public Cause getCause() {
        return null;
    }

    @Override
    public Object getSource() {
        return this.voter;
    }

    public Vote getVote() {
        return this.vote;
    }
}
