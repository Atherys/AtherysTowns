package com.atherys.towns.facade;

import com.atherys.core.utils.Question;
import com.atherys.towns.TownsConfig;
import com.atherys.towns.api.event.PlayerVoteEvent;
import com.atherys.towns.model.Poll;
import com.atherys.towns.model.Vote;
import com.atherys.towns.model.entity.Plot;
import com.atherys.towns.model.entity.Resident;
import com.atherys.towns.model.entity.Town;
import com.atherys.towns.service.PollService;
import com.atherys.towns.service.ResidentService;
import com.atherys.towns.service.TownService;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.util.Identifiable;

import java.util.Objects;
import java.util.Optional;
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

    @Inject
    private TownsConfig config;

    PollFacade() {
    }

    private Set<Player> getPlayersByUUID(Set<UUID> uuidSet) {
        return uuidSet.stream()
                .map(uuid -> Sponge.getServer().getPlayer(uuid))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());
    }

    private Set<UUID> getUUIDsByPlayer(Set<Player> playerSet) {
        return playerSet.stream()
                .map(Identifiable::getUniqueId)
                .collect(Collectors.toSet());
    }

    private Set<Player> findNonVoters(Poll poll) {
        Set<UUID> voted = poll.getVoters().stream()
                .filter(uuid -> poll.getVotes().stream().anyMatch(vote -> vote.getVoter() == uuid))
                .collect(Collectors.toSet());

        return getPlayersByUUID(voted);
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
        Text.Builder invitationText = Text.builder();
        Text townText = Text.of(GOLD, townName, DARK_GREEN, "?");
        Text mayorText = Text.of(GOLD, mayorName, DARK_GREEN);
        invitationText.append(Text.of(DARK_GREEN, "Do you wish to help ", mayorText, DARK_GREEN, " create the town of ", townText, Text.NEW_LINE));
        invitationText.append(Text.of(GOLD, "Warning! ", RED, "If you are currently in a town, you will be removed."));

        return Question.of(invitationText.build())
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

    private Text getPollInfo(Poll poll) {
        Set<Player> voters = getPlayersByUUID(poll.getVoters());
        String voterList = String.join(",", voters.stream().map(User::getName).collect(Collectors.toSet()));

        Text.Builder pollText = Text.builder();
        Text townText = Text.of(GOLD, poll.getPollName(), DARK_GREEN);
        pollText.append(Text.of(DARK_GREEN, "Vote has met minimum required residents that voted yes. Would you like to create ", townText, " now?", Text.NEW_LINE));

        pollText
                .append(townsMsg.createTownsHeader(poll.getPollName()))
                .append(Text.of(DARK_GREEN, "Votes Collected: ", GOLD, poll.getVotes().size(), " of ", poll.getVoters().size(), Text.NEW_LINE
                ));

        pollText.append(Text.of(
                DARK_GREEN, "Voters: ",
                GOLD, voterList,
                Text.NEW_LINE
        ));
        return pollText.build();
    }

    private Question generateMayorQuestion(Poll poll) {
        Question.Builder mayorQuestion = Question.of(getPollInfo(poll))
                .addAnswer(Question.Answer.of(
                        Text.of(TextStyles.BOLD, DARK_GREEN, "Yes"),
                        player -> {
                            createTownFromPoll(poll);
                        }
                ))
                .addAnswer(Question.Answer.of(
                        Text.of(TextStyles.BOLD, DARK_RED, "No"),
                        player -> {
                            Text creationDenied = Text.of(RED, "Mayor has denied creation of the town, Poll cancelled.");
                            sendPollPartyMessage(getPlayersByUUID(poll.getVoters()), creationDenied);
                            Sponge.getServer().getPlayer(poll.getCreator()).ifPresent(mayor -> {
                                townsMsg.info(mayor, creationDenied);
                            });
                            pollService.deletePoll(poll.getId());
                        }
                ));

        if (poll.getVoters().size() > poll.getVotes().size()) {
            mayorQuestion.addAnswer(Question.Answer.of(
                    Text.of(TextStyles.BOLD, DARK_GREEN, "Wait for more"),
                    player -> {
                        poll.setPassed(false);
                        Optional<Player> mayor = Sponge.getServer().getPlayer(poll.getCreator());
                        if (mayor.isPresent()) {
                            Question remainder = generateTownPoll(poll.getPollName(), mayor.get().getName(), poll.getId());
                            findNonVoters(poll).forEach(remainder::pollChat);
                        }
                    }
            ));
        }
        return mayorQuestion.build();
    }

    public void createTownFromPoll(Poll poll) {
        Optional<Player> mayor = Sponge.getServer().getPlayer(poll.getCreator());
        if (mayor.isPresent()) {
            String townName = poll.getPollName();
            Set<Player> voters = getPlayersByUUID(poll.getVoters());

            try {
                Town town = townFacade.createTown(mayor.get(), poll.getPollName(), poll.getHomePlot());
                for (Player player : voters) {
                    Resident resident = residentService.getOrCreate(player);
                    if (resident.getTown() != null) {
                        townFacade.leaveTown(player);
                    }
                    townService.addResidentToTown(player, residentService.getOrCreate(player), town);
                    townsMsg.info(player, "You have been added as a resident to the town of ", GOLD, townName, ".");
                }
            } catch (CommandException e) {
                mayor.get().sendMessage(Objects.requireNonNull(e.getText()));
            }
        }
        pollService.deletePoll(poll.getId());
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
        Optional<Player> mayor = Sponge.getServer().getPlayer(poll.getCreator());
        boolean hasEnoughResidents = poll.getVotes().stream().filter(Vote::hasVotedYes).count() + 1 >= config.MIN_RESIDENTS_TOWN_CREATE;
        boolean isVoteOver = poll.getVoters().size() == poll.getVotes().size();
        boolean townAlreadyCreated = townService.getTownFromName(poll.getPollName()).isPresent();

        if (hasEnoughResidents && !poll.getPassed()) {
            poll.setPassed(true);
            mayor.ifPresent(player -> generateMayorQuestion(poll).pollChat(mayor.get()));
        }

        if (isVoteOver && hasEnoughResidents && !townAlreadyCreated) {
            mayor.ifPresent(player -> generateMayorQuestion(poll).pollChat(mayor.get()));
        } else if (isVoteOver) {
            Text notEnoughText = Text.of(RED, "Not enough residents voted yes. Town creation has failed!");
            sendPollPartyMessage(getPlayersByUUID(poll.getVoters()), notEnoughText);
            mayor.ifPresent(player -> townsMsg.info(player, notEnoughText));
            pollService.deletePoll(poll.getId());
        }
    }
}
