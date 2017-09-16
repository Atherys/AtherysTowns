package com.atherys.towns.commands.town;

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

public class TownInfoCommand extends AbstractTownCommand {

    protected TownInfoCommand() {
        super(
                new String[] { "info", "get" },
                "info [townName]",
                Text.of("Used to get information on either your own or another town."),
                TownRank.Action.NONE,
                false,
                false,
                false,
                true
        );
    }

    @Override
    public CommandResult townsExecute(@Nullable Nation nation, @Nullable Town town, Resident resident, Player player, CommandContext args) {

        Optional<String> townName = args.getOne("townName");

        if ( !townName.isPresent() ) {

            if (resident.town().isPresent()) {
                Town t = resident.town().get();
                player.sendMessage(t.getFormattedInfo());
                return CommandResult.success();
            } else {
                TownMessage.warn(player, Text.of("You are not part of a town!"));
                return CommandResult.empty();
            }

        } else {

            Optional<Town> tOpt;
            Text error;

            if ( townName.get().equalsIgnoreCase("here") ) {
                error = Text.of("You are in the wilderness.");
                tOpt = AtherysTowns.getInstance().getTownManager().getByLocation(player.getLocation());
            } else {
                error = Text.of("No such town exists.");
                tOpt = AtherysTowns.getInstance().getTownManager().getByName(townName.get());
            }

            if ( tOpt.isPresent() ) {
                player.sendMessage(tOpt.get().getFormattedInfo());
            } else {
                TownMessage.warn(player, error);
                return CommandResult.empty();
            }
        }

        return CommandResult.empty();
    }

    @Override
    public CommandSpec getSpec() {
        return  CommandSpec.builder()
                .permission("atherys.towns.commands.town.info")
                .description(Text.of("Used to get information on a town."))
                .arguments(GenericArguments.optional(GenericArguments.remainingJoinedStrings(Text.of("townName"))))
                .executor(this)
                .build();
    }
}
