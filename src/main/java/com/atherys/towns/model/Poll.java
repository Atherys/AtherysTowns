package com.atherys.towns.model;

import com.atherys.towns.model.entity.Plot;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class Poll {
    private UUID id;
    private Set<Vote> votes = new HashSet<>();
    private Set<UUID> voters = new HashSet<>();
    private UUID creator;
    private String pollName;
    private Plot homePlot;
    private int version;
    private boolean passed = true;

    public UUID getId() {
        return this.id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Set<Vote> getVotes() {
        return this.votes;
    }

    public void setVotes(Set<Vote> votes) {
        this.votes = votes;
    }

    public Set<UUID> getVoters() {
        return this.voters;
    }

    public void setVoters(Set<UUID> votes) {
        this.voters = votes;
    }

    public void addVote(Vote vote) {
        this.votes.add(vote);
    }

    public void removeVote(Vote vote) {
        this.votes.remove(vote);
    }

    public UUID getCreator() {
        return this.creator;
    }

    public void setCreator(UUID creator) {
        this.creator = creator;
    }

    public String getPollName() {
        return this.pollName;
    }

    public void setPollName(String pollName) {
        this.pollName = pollName;
    }

    public Plot getHomePlot() {
        return this.homePlot;
    }

    public void setHomePlot(Plot plot) {
        this.homePlot = plot;
    }

    public boolean getPassed() {
        return this.passed;
    }

    public void setPassed(boolean passed) {
        this.passed = passed;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Poll poll = (Poll) o;
        return id.equals(poll.id) &&
                votes.equals(poll.votes) &&
                creator.equals(poll.creator);
    }

    public int hashCode() {
        return Objects.hash(id, creator, pollName, version);
    }

    protected int getVersion() {
        return version;
    }

    protected void setVersion(int version) {
        this.version = version;
    }
}
