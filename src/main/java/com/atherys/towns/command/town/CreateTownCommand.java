package com.atherys.towns.command.town;


import com.atherys.core.command.ParameterizedCommand;
import com.atherys.core.command.PlayerCommand;
import com.atherys.core.command.annotation.Aliases;
import com.atherys.core.command.annotation.Description;
import com.atherys.core.command.annotation.Permission;
import com.atherys.party.AtherysParties;
import com.atherys.party.entity.Party;
import com.atherys.party.facade.PartyFacade;
import com.atherys.towns.AtherysTowns;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.Optional;

@Aliases("create")
@Description("Creates a town.")
@Permission("atherystowns.town.create")
public class CreateTownCommand implements PlayerCommand, ParameterizedCommand {
    @Override
    public CommandElement[] getArguments() {
        return new CommandElement[]{
                GenericArguments.string(Text.of("name"))
        };
    }

    @Override
    public CommandResult execute(Player src, CommandContext args) throws CommandException {
        String townName = args.<String>getOne("name").orElse("");
        PartyFacade partyFacade = AtherysParties.getInstance().getPartyFacade();
        Optional<Party> party = partyFacade.getPlayerParty(src);
        if (party.isPresent()) {
            AtherysTowns.getInstance().getTownFacade().sendCreateTownPoll(townName, partyFacade.getOnlinePartyMembers(party.get()), src);
        } else {
            AtherysTowns.getInstance().getTownFacade().createTown(src, townName);
        }

        return CommandResult.success();
    }
}
