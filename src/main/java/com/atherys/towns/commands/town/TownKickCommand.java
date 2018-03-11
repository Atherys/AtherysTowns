package com.atherys.towns.commands.town;

import com.atherys.towns.commands.TownsSimpleCommand;
import com.atherys.towns.nation.Nation;
import com.atherys.towns.permissions.actions.TownActions;
import com.atherys.towns.permissions.ranks.TownRanks;
import com.atherys.towns.resident.Resident;
import com.atherys.towns.town.Town;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;

import javax.annotation.Nullable;
import java.util.Optional;

public class TownKickCommand extends TownsSimpleCommand {

    private static TownKickCommand instance = new TownKickCommand();

    public static TownKickCommand getInstance () {
        return instance;
    }

    @Override
    protected CommandResult execute ( Player player, CommandContext args, Resident resident, @Nullable Town town, @Nullable Nation nation ) {
        Optional<User> user = args.getOne( "resident" );
        if ( !user.isPresent() ) return CommandResult.empty();

        if ( town == null ) return CommandResult.empty();
        Optional<Resident> res = town.getResident( user.get().getUniqueId() );

        // if the resident is present and the rank of the resident trying to kick them is higher
        if ( res.isPresent() && resident.getTownRank().isRankGreaterThan( res.get().getTownRank() ) || player.hasPermission( "atherystowns.admin.kick_any" ) ) {
            res.get().setTown( null, TownRanks.NONE );
            town.warnResidents( Text.of( user.get().getName(), " has been kicked from the town by ", resident.getName() ) );
        }

        return CommandResult.empty();
    }

    @Override
    public CommandSpec getSpec () {
        return CommandSpec.builder()
                .description( Text.of( "Used to kick somebody from the town." ) )
                .permission( TownActions.KICK_PLAYER.getPermission() )
                .arguments(
                        GenericArguments.onlyOne( GenericArguments.user( Text.of( "resident" ) ) )
                )
                .executor( this )
                .build();
    }
}
