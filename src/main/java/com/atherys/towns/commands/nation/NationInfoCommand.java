package com.atherys.towns.commands.nation;

import com.atherys.towns.commands.TownsSimpleCommand;
import com.atherys.towns.managers.NationManager;
import com.atherys.towns.messaging.TownMessage;
import com.atherys.towns.nation.Nation;
import com.atherys.towns.resident.Resident;
import com.atherys.towns.town.Town;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import javax.annotation.Nullable;
import java.util.Optional;

public class NationInfoCommand extends TownsSimpleCommand {

    private static NationInfoCommand instance = new NationInfoCommand();

    public static NationInfoCommand getInstance () {
        return instance;
    }

    @Override
    protected CommandResult execute ( Player player, CommandContext args, Resident resident, @Nullable Town town, @Nullable Nation nation ) {

        String nationName = args.<String>getOne( "nation" ).orElseGet( () -> {
            Optional<Nation> residentNation = resident.getNation();
            if ( residentNation.isPresent() ) return residentNation.get().getName();
            return "None";
        } );

        if ( nationName.equals( "None" ) ) return CommandResult.success();

        Optional<Nation> nationOptional = NationManager.getInstance().getFirstByName( nationName );

        if ( nationOptional.isPresent() ) {
            nationOptional.get().createView().ifPresent( view -> view.show( player ) );
        } else {
            TownMessage.warn( player, "No nation found." );
        }

        return CommandResult.success();
    }

    @Override
    public CommandSpec getSpec () {
        return CommandSpec.builder()
                .description( Text.of( "Used to get info on a nation." ) )
                .arguments( GenericArguments.optional( GenericArguments.remainingJoinedStrings( Text.of( "nation" ) ) ) )
                .executor( this )
                .build();
    }
}
