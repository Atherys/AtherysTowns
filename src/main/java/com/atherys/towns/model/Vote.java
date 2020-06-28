package com.atherys.towns.model;

import org.spongepowered.api.entity.living.player.Player;

import java.util.Objects;
import java.util.UUID;

public class Vote {

    private Long id;
    private UUID pollId;
    private Player voter;
    private boolean votedYes;
    private int version;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UUID getPollId() {
        return this.pollId;
    }

    public void setPollId(UUID pollId) {
        this.pollId = pollId;
    }

    public Player getVoter() {
        return this.voter;
    }

    public void setVoter(Player voter) {
        this.voter = voter;
    }

    public boolean hasVotedYes() {
        return this.votedYes;
    }

    public void setVotedYes(boolean votedYes) {
        this.votedYes = votedYes;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vote vote = (Vote) o;
        return id.equals(vote.id) &&
                pollId.equals(vote.pollId) &&
                voter.equals(vote.voter) &&
                votedYes == vote.votedYes;
    }

    public int hashCode() {
        return Objects.hash(id, pollId, voter, votedYes, version);
    }

    protected int getVersion() {
        return version;
    }

    protected void setVersion(int version) {
        this.version = version;
    }
}
