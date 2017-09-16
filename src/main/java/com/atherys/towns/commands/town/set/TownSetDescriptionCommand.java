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

import javax.annotation.Nullable;

public class TownSetDescriptionCommand extends AbstractTownSetCommand {

    TownSetDescriptionCommand() {
        super(
                new String[] { "desc", "description" },
                "desc <description>",
                Text.of("Used to change the town description."),
                TownRank.Action.SET_DESCRIPTION
        );
    }

    @Override
    public CommandResult townsExecute(@Nullable Nation nation, @Nullable Town town, Resident resident, Player player, CommandContext args) {
        if ( town == null ) return CommandResult.empty();

        String desc = (String) args.getOne("newDescription").orElse( town.description() );
        town.setDescription( desc );
        town.informResidents( Text.of( "Town Description changed to ", town.description() ) );

        return CommandResult.success();
    }

    @Override
    public CommandSpec getSpec() {
        return  CommandSpec.builder()
                .permission("atherys.towns.commands.town.set.description")
                .description(Text.of("Used to set the Description of the town."))
                .arguments(GenericArguments.remainingJoinedStrings(Text.of("newDescription")))
                .executor(this)
                .build();
    }
}
