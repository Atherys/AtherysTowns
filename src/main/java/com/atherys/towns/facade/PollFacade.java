package com.atherys.towns.facade;

import com.atherys.core.utils.Question;
import com.atherys.towns.api.event.PlayerVoteEvent;
import com.atherys.towns.model.Poll;
import com.atherys.towns.model.Vote;
import com.atherys.towns.model.entity.Plot;
import com.atherys.towns.model.entity.Town;
import com.atherys.towns.service.PollService;
import com.atherys.towns.service.ResidentService;
import com.atherys.towns.service.TownService;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.util.Identifiable;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.spongepowered.api.text.format.TextColors.*;

@Singleton
public class PollFacade {

    @Inject
    private TownService townService;

    @Inject
    private PollService pollService;

    @Inject
    private ResidentService residentService;

    @Inject
    private TownsMessagingFacade townsMsg;

    @Inject
    private TownFacade townFacade;

    PollFacade() {
    }

    private Set<Player> getPlayersByUUID(Set<UUID> uuidSet) {
        return uuidSet.stream()
                .map(uuid -> Sponge.getServer().getPlayer(uuid).get())
                .collect(Collectors.toSet());
    }

    private Set<UUID> getUUIDsByPlayer(Set<Player> playerSet) {
        return playerSet.stream()
                .map(Identifiable::getUniqueId)
                .collect(Collectors.toSet());
    }

    public void sendPollPartyMessage(Set<Player> party, Text msg) {
        party.forEach(player -> {
            townsMsg.info(player, msg);
        });
    }

    private void postVoteEvent(Player player, boolean voteResult, UUID pollId) {
        Vote vote = new Vote();
        vote.setVotedYes(voteResult);
        vote.setVoter(player.getUniqueId());
        vote.setPollId(pollId);
        pollService.addVoteToPoll(vote, pollId);
        Sponge.getEventManager().post(new PlayerVoteEvent(player, vote));
    }

    private Question generateTownPoll(String townName, String mayorName, UUID id) {
        Text townText = Text.of(GOLD, townName, DARK_GREEN, "?");
        Text mayorText = Text.of(GOLD, mayorName, DARK_GREEN);
        Text invitationText = townsMsg.formatInfo("Do you wish to help ", mayorText, " create the town of ", townText);

        return Question.of(invitationText)
                .addAnswer(Question.Answer.of(
                        Text.of(TextStyles.BOLD, DARK_GREEN, "Yes"),
                        player -> {
                            postVoteEvent(player, true, id);
                        }
                ))
                .addAnswer(Question.Answer.of(
                        Text.of(TextStyles.BOLD, DARK_RED, "No"),
                        player -> {
                            postVoteEvent(player, false, id);
                        }
                ))
                .build();
    }

    public void createTownFromPoll(Poll poll, Plot homePlot) {
        Player mayor = Sponge.getServer().getPlayer(poll.getCreator()).get();
        String townName = poll.getPollName();
        Set<Player> voters = getPlayersByUUID(poll.getVoters());

        try {
            townFacade.createTown(mayor, poll.getPollName(), homePlot);
            Town town = townService.getTownFromName(townName).get();
            voters.forEach(player -> {
                townService.addResidentToTown(player, residentService.getOrCreate(player), town);
                townsMsg.info(player, "You have been added as a resident to the town of ", GOLD, townName, ".");
            });
        } catch (CommandException e) {
            mayor.sendMessage(Objects.requireNonNull(e.getText()));
        }
    }

    public void sendCreateTownPoll(String townName, Set<Player> voters, Player mayor, Plot homePlot) {
        voters.remove(mayor);
        UUID pollId = pollService.createPoll(mayor.getUniqueId(), townName, getUUIDsByPlayer(voters), homePlot);

        Question pollQuestion = generateTownPoll(townName, mayor.getName(), pollId);

        Text startPollMsg = Text.of("A vote to found the town of ", GOLD, townName, DARK_GREEN, " has begun!");
        sendPollPartyMessage(voters, startPollMsg);
        townsMsg.info(mayor, startPollMsg);

        voters.forEach(pollQuestion::pollChat);
    }

    public void onPlayerVote(PlayerVoteEvent event) {
        Vote vote = event.getVote();
        Poll poll = pollService.getPollById(vote.getPollId());
        Player mayor = Sponge.getServer().getPlayer(poll.getCreator()).orElse(null);

        Set<Player> voters = getPlayersByUUID(poll.getVoters());

        if (!vote.hasVotedYes()) {
            poll.setPassed(false);
            Text pollFailedmsg = Text.of(RED, "Someone responded No! The town of ", GOLD, poll.getPollName(), RED, " will not be founded!");
            sendPollPartyMessage(voters, pollFailedmsg);
            townsMsg.error(mayor, pollFailedmsg);
        } else if (poll.getPassed() && poll.getVotes().size() == poll.getVotesNeeded()) {
            createTownFromPoll(poll, poll.getHomePlot());
        }
    }
}
