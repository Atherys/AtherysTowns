package com.atherys.towns.model;

import java.util.Objects;
import java.util.UUID;

public class Vote {

    private Long id;
    private Poll poll;
    private UUID voter;
    private boolean votedYes;
    private int version;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Poll getPoll() {
        return this.poll;
    }

    public void setPoll(Poll poll) {
        this.poll = poll;
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

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vote vote = (Vote) o;
        return id.equals(vote.id) &&
                poll.equals(vote.poll) &&
                voter.equals(vote.voter) &&
                votedYes == vote.votedYes;
    }

    public int hashCode() {
        return Objects.hash(id, poll, voter, votedYes, version);
    }

    protected int getVersion() {
        return version;
    }

    protected void setVersion(int version) {
        this.version = version;
    }
}
