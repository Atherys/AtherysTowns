package com.atherys.towns.commands.town.set;

import com.atherys.towns.commands.TownsSimpleCommand;
import com.atherys.towns.nation.Nation;
import com.atherys.towns.resident.Resident;
import com.atherys.towns.permissions.actions.TownAction;
import com.atherys.towns.town.Town;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import javax.annotation.Nullable;

public class TownSetDescriptionCommand extends TownsSimpleCommand {

    private static TownsSimpleCommand instance = new TownSetDescriptionCommand();

    public static TownsSimpleCommand getInstance() {
        return instance;
    }

    @Override
    protected CommandResult execute(Player player, CommandContext args, Resident resident, @Nullable Town town, @Nullable Nation nation) {
        if ( town == null ) return CommandResult.empty();

        String desc = (String) args.getOne("newDescription").orElse( town.getDescription() );
        town.setDescription( desc );
        town.informResidents( Text.of( "Town Description changed to ", town.getDescription() ) );

        return CommandResult.success();
    }

    @Override
    public CommandSpec getSpec() {
        return CommandSpec.builder()
                .description( Text.of( "Used to change the description of the town" ) )
                .permission( TownAction.SET_DESCRIPTION.getPermission() )
                .arguments(
                        GenericArguments.remainingJoinedStrings(Text.of("newDescription"))
                )
                .executor( this )
                .build();
    }
}
