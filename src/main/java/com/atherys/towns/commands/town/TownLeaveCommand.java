package com.atherys.towns.commands.town;

import com.atherys.towns.commands.TownsSimpleCommand;
import com.atherys.towns.nation.Nation;
import com.atherys.towns.permissions.actions.TownActions;
import com.atherys.towns.permissions.ranks.TownRanks;
import com.atherys.towns.resident.Resident;
import com.atherys.towns.town.Town;
import com.atherys.towns.utils.Question;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import javax.annotation.Nullable;
import java.util.Optional;

public class TownLeaveCommand extends TownsSimpleCommand {

    private static TownLeaveCommand instance = new TownLeaveCommand();

    public static TownLeaveCommand getInstance() {
        return instance;
    }

    @Override
    protected CommandResult execute(Player player, CommandContext args, Resident resident, @Nullable Town town, @Nullable Nation nation) {

        leaveTown( resident );

        return CommandResult.success();
    }

    @Override
    public CommandSpec getSpec() {
        return CommandSpec.builder()
                .description( Text.of( "Used to leave a town. If you are not part of a town, this command will not function." ) )
                .permission( TownActions.LEAVE_TOWN.getPermission() )
                .executor( new TownLeaveCommand() )
                .build();
    }

    private void leaveTown( Resident resident ) {
        Optional<Player> player = resident.getPlayer();
        if ( !player.isPresent() ) return;

        Question leave = Question.of( Text.of("Would you like to leave your current town?") )
                .addAnswer( Question.Answer.of( Text.of( TextColors.GREEN, "Yes"), player1 -> {
                    if ( resident.getTown().isPresent() ) {
                        resident.setTown(null, TownRanks.NONE);
                        resident.getTown().get().warnResidents( Text.of( player.get().getName() + " has left the town.") );
                    }
                }))
                .addAnswer( Question.Answer.of( Text.of( TextColors.RED, "No" ), player1 -> {} ))
                .build();

        leave.pollChat( player.get() );
    }
}
