package com.atherys.towns.commands.town.set;

import com.atherys.towns.AtherysTowns;
import com.atherys.towns.TownsConfig;
import com.atherys.towns.commands.TownsSimpleCommand;
import com.atherys.towns.messaging.TownMessage;
import com.atherys.towns.nation.Nation;
import com.atherys.towns.permissions.actions.TownActions;
import com.atherys.towns.resident.Resident;
import com.atherys.towns.town.Town;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import javax.annotation.Nullable;

public class TownSetNameCommand extends TownsSimpleCommand {

    private static TownSetNameCommand instance = new TownSetNameCommand();

    public static TownSetNameCommand getInstance () {
        return instance;
    }

    @Override
    protected CommandResult execute ( Player player, CommandContext args, Resident resident, @Nullable Town town, @Nullable Nation nation ) {
        if ( town == null ) return CommandResult.empty();

        String name = (String) args.getOne( "newName" ).orElse( town.getName() );
        if ( name.length() > AtherysTowns.getConfig().TOWN.MAX_NAME_LENGTH ) {
            TownMessage.warn( player, "Town name must not exceed " + AtherysTowns.getConfig().TOWN.MAX_NAME_LENGTH + " symbols." );
            return CommandResult.empty();
        }
        town.setName( name );
        town.informResidents( Text.of( "Town name changed to ", name ) );

        return CommandResult.success();
    }

    @Override
    public CommandSpec getSpec () {
        return CommandSpec.builder()
                .description( Text.of( "Used to change the name of the town." ) )
                .permission( TownActions.SET_NAME.getPermission() )
                .arguments(
                        GenericArguments.remainingJoinedStrings( Text.of( "newName" ) )
                )
                .executor( this )
                .build();
    }
}
