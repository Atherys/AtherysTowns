package com.atherys.towns.commands.town;

import com.atherys.towns.commands.TownsSimpleCommand;
import com.atherys.towns.commands.TownsValues;
import com.atherys.towns.messaging.TownMessage;
import com.atherys.towns.nation.Nation;
import com.atherys.towns.resident.Resident;
import com.atherys.towns.permissions.actions.TownAction;
import com.atherys.towns.town.Town;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import javax.annotation.Nullable;

public class TownBorderCommand extends TownsSimpleCommand {

    private static TownBorderCommand instance = new TownBorderCommand();

    @Override
    protected CommandResult execute(Player player, CommandContext args, Resident resident, @Nullable Town town, @Nullable Nation nation) {
        if ( !TownsValues.get( player.getUniqueId(), TownsValues.TownsKey.TOWN_BORDERS ).isPresent() ) {
            TownsValues.set( player.getUniqueId(), TownsValues.TownsKey.TOWN_BORDERS, true );
            TownMessage.inform( player, "Now showing town borders.");
        } else {
            TownsValues.remove( player.getUniqueId(), TownsValues.TownsKey.TOWN_BORDERS);
            TownMessage.warn( player, "No longer showing town borders.");
        }

        return CommandResult.success();
    }

    @Override
    public CommandSpec getSpec() {
        return CommandSpec.builder()
                .description( Text.of( "Used to toggle the town border" ) )
                .permission( TownAction.SHOW_TOWN_BORDER.getPermission() )
                .executor( this )
                .build();
    }

    public static TownBorderCommand getInstance() {
        return instance;
    }
}
