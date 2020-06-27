package com.atherys.towns.model.entity;

import com.atherys.core.db.Identifiable;

import javax.annotation.Nonnull;
import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Entity
public class Poll implements Identifiable<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @OneToMany(mappedBy = "poll", fetch = FetchType.EAGER)
    private Set<Vote> votes = new HashSet<>();

    private UUID creator;

    private String pollName;
    private int votesNeeded;

    @Version
    private int version;

    @Nonnull
    @Override
    public Long getId() { return this.id; }
    public void setId(Long id) {
        this.id = id;
    }

    public Set<Vote> getVotes() { return this.votes; }
    public void setVotes(Set<Vote> votes) { this.votes = votes; }

    public void addVote(Vote vote) { this.votes.add(vote); }
    public void removeVote(Vote vote) { this.votes.remove(vote); }

    public UUID getCreator() { return this.creator; }
    public void setCreator(UUID creator) { this.creator = creator; }

    public String getPollName() { return this.pollName; }
    public void setPollName(String pollName) { this.pollName = pollName;}

    public int getVotesNeeded() { return this.votesNeeded; }
    public void setVotesNeeded(int votesNeeded) { this.votesNeeded = votesNeeded; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Poll poll = (Poll) o;
        return id.equals(poll.id) &&
                votes.equals(poll.votes) &&
                creator.equals(poll.creator) &&
                votesNeeded == poll.votesNeeded;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, votes, creator, pollName, votesNeeded, version);
    }

    protected int getVersion() {
        return version;
    }

    protected void setVersion(int version) {
        this.version = version;
    }
}
