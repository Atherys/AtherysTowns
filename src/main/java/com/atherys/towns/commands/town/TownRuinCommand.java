package com.atherys.towns.commands.town;

import com.atherys.towns.messaging.TownMessage;
import com.atherys.towns.nation.Nation;
import com.atherys.towns.resident.Resident;
import com.atherys.towns.resident.ranks.TownRank;
import com.atherys.towns.town.Town;
import com.atherys.towns.town.TownStatus;
import com.atherys.towns.utils.Question;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import javax.annotation.Nullable;

public class TownRuinCommand extends AbstractTownCommand {

    TownRuinCommand() {
        super(
                new String[] { "ruin", "destroy" },
                "ruin",
                Text.of("Used to destroy the town. Doing this will eject all residents and unclaim all plots. The town would cease to exist."),
                TownRank.Action.RUIN_TOWN,
                true,
                false,
                true,
                true
        );
    }

    @Override
    public CommandResult townsExecute(@Nullable Nation nation, @Nullable Town town, Resident resident, Player player, CommandContext args) {
        if ( town == null ) return CommandResult.empty();

        player.sendBookView( Question.asBookView( Question.asText(
                Text.of("Are you sure you want to destroy your town? Doing this will eject all residents and unclaim all plots."),
                Question.Type.YES_NO,
                (commandSource -> {
                    if ( town.status() == TownStatus.CAPITAL ) {
                        TownMessage.warn( player, "You cannot destroy the capital of a nation!");
                        return;
                    }
                    town.ruin();
                })
        ) ) );

        return CommandResult.success();
    }

    @Override
    public CommandSpec getSpec() {
        return CommandSpec.builder()
                .permission("atherys.commands.town.ruin")
                .description(Text.of("Used to leave a town to ruins."))
                .executor(this)
                .build();

    }
}
