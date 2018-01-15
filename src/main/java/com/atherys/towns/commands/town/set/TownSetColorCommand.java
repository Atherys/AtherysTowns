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
import org.spongepowered.api.text.format.TextColor;

import javax.annotation.Nullable;

public class TownSetColorCommand extends TownsSimpleCommand {

    private static TownSetColorCommand instance = new TownSetColorCommand();

    public static TownSetColorCommand getInstance() {
        return instance;
    }

    @Override
    protected CommandResult execute(Player player, CommandContext args, Resident resident, @Nullable Town town, @Nullable Nation nation) {
        if ( town == null ) return CommandResult.empty();

        TextColor color = (TextColor) args.getOne("newColor").orElse( town.getColor() );
        town.setColor(color);
        town.informResidents( Text.of( "Town Color changed to ", town.getColor(), town.getColor().getName().replace('_',' ') ) );

        return CommandResult.success();
    }

    @Override
    public CommandSpec getSpec() {
        return CommandSpec.builder()
                .description( Text.of( "Used to change the color of the town." ) )
                .permission( TownActions.SET_COLOR.getPermission() )
                .arguments(
                        GenericArguments.catalogedElement( Text.of("newColor"), TextColor.class )
                )
                .executor( this )
                .build();
    }
}
