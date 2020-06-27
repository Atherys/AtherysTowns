package com.atherys.towns.service;

import com.atherys.towns.model.entity.Poll;
import com.atherys.towns.model.entity.Vote;
import com.atherys.towns.persistence.PollRepository;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.spongepowered.api.entity.living.player.Player;

import java.util.HashSet;
import java.util.Set;

@Singleton
public class PollService {

    private PollRepository pollRepository;

    @Inject
    PollService(PollRepository pollRepository){ this.pollRepository = pollRepository; };

    public Poll createPoll(Player pollCreator, String pollName, int votesNeededToPass) {
        Poll poll = new Poll();

        poll.setCreator(pollCreator.getUniqueId());
        poll.setPollName(pollName);
        poll.setVotesNeeded(votesNeededToPass);

        pollRepository.saveOne(poll);
        return poll;
    }

    public void deletePoll(Poll poll) {
        pollRepository.deleteOne(poll);
    }

    public Set<Vote> getPollVotes(Poll poll) {
        Set<Vote> votes = new HashSet<>();
        pollRepository.findById(poll.getId()).ifPresent(poll1 -> {
            votes.addAll(poll1.getVotes());
        });
        return votes;
    }

    public void addVoteToPollByName(Vote vote, String pollName) {
        pollRepository.findByName(pollName).ifPresent(poll1 -> {
            poll1.addVote(vote);
            pollRepository.saveOne(poll1);
        });
    }

    public void addVoteToPoll(Vote vote, Poll poll) {
        poll.addVote(vote);
        pollRepository.saveOne(poll);
    }

}
