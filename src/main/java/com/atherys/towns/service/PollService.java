package com.atherys.towns.service;

import com.atherys.towns.model.Poll;
import com.atherys.towns.model.Vote;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.spongepowered.api.entity.living.player.Player;

import java.util.HashMap;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

@Singleton
public class PollService {

    private HashMap<UUID, Poll> pollCache = new HashMap<>();

    @Inject
    PollService() {
    }

    public Poll getPollById(UUID id) {
        return pollCache.get(id);
    }

    public UUID createPoll(Player pollCreator, String pollName, Set<Player> voters) {
        Poll poll = new Poll();
        long id = new Random().nextLong();
        poll.setId(id);
        poll.setVoters(voters);
        poll.setCreator(pollCreator);
        poll.setPollName(pollName);
        poll.setVotesNeeded(voters.size());

        UUID pollUUID = UUID.randomUUID();

        while (pollCache.containsKey(pollUUID)) {
            pollUUID = UUID.randomUUID();
        }

        pollCache.put(pollUUID, poll);
        return pollUUID;
    }

    public void deletePoll(UUID id) {
        pollCache.remove(id);
    }

    public Set<Vote> getPollVotes(UUID id) {
        return pollCache.get(id).getVotes();
    }

    public void addVoteToPoll(Vote vote, UUID id) {
        pollCache.get(id).addVote(vote);
    }

}
