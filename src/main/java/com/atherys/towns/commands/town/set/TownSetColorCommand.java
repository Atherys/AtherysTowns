package com.atherys.towns.commands.town.set;

import com.atherys.towns.nation.Nation;
import com.atherys.towns.resident.Resident;
import com.atherys.towns.resident.ranks.TownRank;
import com.atherys.towns.town.Town;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;

import javax.annotation.Nullable;

public class TownSetColorCommand extends AbstractTownSetCommand {

    TownSetColorCommand () {
        super(
                new String[] { "color" },
                "color <color>",
                Text.of( "Used to change the color of the town." ),
                TownRank.Action.SET_COLOR
        );
    }

    @Override
    public CommandResult townsExecute(@Nullable Nation nation, @Nullable Town town, Resident resident, Player player, CommandContext args) {
        if ( town == null ) return CommandResult.empty();

        TextColor color = (TextColor) args.getOne("newColor").orElse( town.color() );
        town.setColor(color);
        town.informResidents( Text.of( "Town Color changed to ", town.color(), town.color().getName().replace('_',' ') ) );

        return CommandResult.success();
    }

    @Override
    public CommandSpec getSpec() {
        return  CommandSpec.builder()
                .permission("atherys.towns.commands.town.set.color")
                .description(Text.of("Used to set the Description of the town."))
                .arguments( GenericArguments.catalogedElement( Text.of("newColor"), TextColor.class ) )
                .executor(this)
                .build();
    }
}
