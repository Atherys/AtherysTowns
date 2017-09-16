package com.atherys.towns.commands.town.set;

import com.atherys.towns.AtherysTowns;
import com.atherys.towns.messaging.TownMessage;
import com.atherys.towns.nation.Nation;
import com.atherys.towns.resident.Resident;
import com.atherys.towns.resident.ranks.TownRank;
import com.atherys.towns.town.Town;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

public class TownSetNationCommand extends AbstractTownSetCommand {

    TownSetNationCommand() {
        super(
                new String[] { "nation" },
                "nation <newNation>",
                Text.of("Used to change the nation your town belongs to."),
                TownRank.Action.SET_NATION
        );
    }

    @Override
    public CommandResult townsExecute(@Nullable Nation nation, @Nullable Town town, Resident resident, Player player, CommandContext args) {
        if ( town == null ) return CommandResult.empty();

        Optional<Nation> n = AtherysTowns.getInstance().getNationManager().getByName( args.<String>getOne("nation").orElse(UUID.randomUUID().toString()) );

        if ( n.isPresent() ) {
            n.get().addTown(town);
            TownMessage.informAll(Text.of(town.getName() + " has joined the nation of " + n.get().name() ));
        } else {
            TownMessage.warn(player, "You must provide a valid nation to join.");
        }

        return CommandResult.empty();
    }

    @Override
    public CommandSpec getSpec() {
        return  CommandSpec.builder()
                .permission("atherys.towns.commands.town.set.nation")
                .description(Text.of("Used to set the nation of the town."))
                .arguments( GenericArguments.remainingJoinedStrings( Text.of("nation") ) )
                .executor(this)
                .build();
    }
}
