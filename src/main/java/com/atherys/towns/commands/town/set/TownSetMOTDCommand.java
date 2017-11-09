package com.atherys.towns.commands.town.set;

import com.atherys.towns.commands.TownsSimpleCommand;
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

public class TownSetMOTDCommand extends TownsSimpleCommand {

    private static TownSetMOTDCommand instance = new TownSetMOTDCommand();

    public static TownSetMOTDCommand getInstance() {
        return instance;
    }

    @Override
    protected CommandResult execute(Player player, CommandContext args, Resident resident, @Nullable Town town, @Nullable Nation nation) {
        if ( town == null ) return CommandResult.empty();

        String motd = (String) args.getOne("newMOTD").orElse( town.getMOTD() );
        town.setMOTD( motd );
        town.informResidents( Text.of( "Town MOTD changed to ", town.getMOTD() ) );

        return CommandResult.success();
    }

    @Override
    public CommandSpec getSpec() {
        return CommandSpec.builder()
                .description( Text.of( "Used to change the MOTD of the town." ) )
                .permission( TownActions.SET_MOTD.getPermission() )
                .arguments(
                        GenericArguments.remainingJoinedStrings(Text.of("newMOTD"))
                )
                .executor( new TownSetMOTDCommand() )
                .build();
    }
}
