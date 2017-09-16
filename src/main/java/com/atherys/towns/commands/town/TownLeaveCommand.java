package com.atherys.towns.commands.town;

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

public class TownLeaveCommand extends AbstractTownCommand {

    TownLeaveCommand() {
        super(
                new String[] { "leave" },
                "leave",
                Text.of("Use this to leave your town."),
                TownRank.Action.LEAVE_TOWN,
                true,
                false,
                true,
                true
        );
    }

    @Override
    public CommandResult townsExecute(@Nullable Nation nation, @Nullable Town town, Resident resident, Player player, CommandContext args) {

        if ( resident.townRank().equals(TownRank.MAYOR) ) {
            TownMessage.warn( player, "You are the mayor of the town. You can either leave the town for dead ( /t ruin ) or promote another mayor ( /t mayor <playerName> ) and then leave." );
            return CommandResult.empty();
        }

        resident.leaveTown();
        return CommandResult.success();
    }

    @Override
    public CommandSpec getSpec() {
        return  CommandSpec.builder()
                .permission("atherys.towns.commands.town.leave")
                .description(Text.of("Used to leave a town."))
                .executor(this)
                .build();
    }
}
