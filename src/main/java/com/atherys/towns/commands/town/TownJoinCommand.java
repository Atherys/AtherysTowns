package com.atherys.towns.commands.town;

import com.atherys.towns.AtherysTowns;
import com.atherys.towns.messaging.TownMessage;
import com.atherys.towns.nation.Nation;
import com.atherys.towns.plot.PlotFlags;
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

public class TownJoinCommand extends AbstractTownCommand {

    TownJoinCommand() {
        super(
                new String[] { "join" },
                "join <townName>",
                Text.of( "Used to join a town." ),
                TownRank.Action.JOIN_TOWN,
                false,
                false,
                true,
                true
        );
    }

    @Override
    public CommandResult townsExecute(@Nullable Nation nation, @Nullable Town town, Resident resident, Player player, CommandContext args) {

        Optional<Town> tOpt = AtherysTowns.getInstance().getTownManager().getByName(args.<String>getOne("townName").orElse(UUID.randomUUID().toString()));

        if ( !tOpt.isPresent() ) {
            TownMessage.warn(player, "That town doesn't exist!");
            return CommandResult.empty();
        } else {

            if ( town != null ) {
                TownMessage.warn(player, "You are already part of a town!");
                return CommandResult.empty();
            }

            Town t = tOpt.get();

            if ( t.flags().get(PlotFlags.Flag.JOIN) == PlotFlags.Extent.ALL ) {
                t.inviteResident(resident);
            } else {
                TownMessage.warn(player, "The town you are trying to join requires an invitation.");
                return CommandResult.empty();
            }
        }

        return CommandResult.empty();
    }

    @Override
    public CommandSpec getSpec() {
        return  CommandSpec.builder()
                .permission("atherys.town.command.town.join")
                .description(Text.of("Used to join a town with an open door policy."))
                .arguments(GenericArguments.remainingJoinedStrings(Text.of("townName")))
                .executor(this)
                .build();
    }
}
