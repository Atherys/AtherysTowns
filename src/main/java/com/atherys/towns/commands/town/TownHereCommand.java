package com.atherys.towns.commands.town;

import com.atherys.towns.managers.TownManager;
import com.atherys.towns.messaging.TownMessage;
import com.atherys.towns.nation.Nation;
import com.atherys.towns.resident.Resident;
import com.atherys.towns.resident.ranks.TownRank;
import com.atherys.towns.town.Town;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import javax.annotation.Nullable;
import java.util.Optional;

public class TownHereCommand extends AbstractTownCommand {

    TownHereCommand() {
        super(
                new String[] { "here" },
                "here",
                Text.of("Used to get the town you are currently standing in."),
                TownRank.Action.NONE,
                false,
                false,
                false,
                true
        );
    }

    @Override
    public CommandResult townsExecute(@Nullable Nation nation, @Nullable Town town, Resident resident, Player player, CommandContext args) {
        Optional<Town> tHere = TownManager.getInstance().getByLocation(player.getLocation());
        if ( tHere.isPresent() ) {
            player.sendMessage(tHere.get().getFormattedInfo());
            return CommandResult.success();
        } else {
            TownMessage.warn(player, "You are in the wilderness.");
        }
        return CommandResult.empty();
    }

    @Override
    public CommandSpec getSpec() {
        return  CommandSpec.builder()
                .permission("atherys.towns.commands.town.here")
                .description(Text.of("Used to get information on the town you are currently standing in.") )
                .executor(this)
                .build();
    }
}
