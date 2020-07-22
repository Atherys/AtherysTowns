package com.atherys.towns.model;

import java.util.UUID;

public class Vote {

    private UUID pollId;
    private UUID voter;
    private boolean votedYes;

    public UUID getPollId() {
        return this.pollId;
    }

    public void setPollId(UUID pollId) {
        this.pollId = pollId;
    }

    public UUID getVoter() {
        return this.voter;
    }

    public void setVoter(UUID voter) {
        this.voter = voter;
    }

    public boolean hasVotedYes() {
        return this.votedYes;
    }

    public void setVotedYes(boolean votedYes) {
        this.votedYes = votedYes;
    }
}
