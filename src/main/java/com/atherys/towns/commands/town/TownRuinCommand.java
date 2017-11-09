package com.atherys.towns.commands.town;

import com.atherys.towns.commands.TownsSimpleCommand;
import com.atherys.towns.messaging.TownMessage;
import com.atherys.towns.nation.Nation;
import com.atherys.towns.permissions.actions.TownActions;
import com.atherys.towns.resident.Resident;
import com.atherys.towns.town.Town;
import com.atherys.towns.town.TownStatus;
import com.atherys.towns.utils.Question;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import javax.annotation.Nullable;

public class TownRuinCommand extends TownsSimpleCommand {

    private static TownRuinCommand instance = new TownRuinCommand();

    public static TownRuinCommand getInstance() {
        return instance;
    }

    @Override
    protected CommandResult execute(Player player, CommandContext args, Resident resident, @Nullable Town town, @Nullable Nation nation) {
        if ( town == null ) return CommandResult.empty();

        player.sendBookView( Question.asBookView( Question.asText(
                Text.of("Are you sure you want to destroy your town? Doing this will eject all residents and unclaim all plots."),
                Question.Type.YES_NO,
                (commandSource -> {
                    if ( town.getStatus() == TownStatus.CAPITAL ) {
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
                .description( Text.of( "Used to ruin a town. This will remove the town from the game, unclaim all it's plots and leave all residents homeless." ) )
                .permission( TownActions.RUIN_TOWN.getPermission() )
                .executor( new TownRuinCommand() )
                .build();
    }
}
