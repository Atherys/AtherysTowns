package com.atherys.towns.commands.town.set;

import com.atherys.towns.Settings;
import com.atherys.towns.messaging.TownMessage;
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

public class TownSetNameCommand extends AbstractTownSetCommand {

    TownSetNameCommand() {
        super(
                new String[] { "name" },
                "name [newName]",
                Text.of("Used to change the name of the town."),
                TownRank.Action.SET_NAME
        );
    }

    @Override
    public CommandResult townsExecute(@Nullable Nation nation, @Nullable Town town, Resident resident, Player player, CommandContext args) {
        if ( town == null ) return CommandResult.empty();

        String name = (String) args.getOne("newName").orElse( town.getName() );
        if ( name.length() > Settings.MAX_TOWN_NAME_LENGTH ) {
            TownMessage.warn(player, "Town name must not exceed " + Settings.MAX_TOWN_NAME_LENGTH + " symbols.");
            return CommandResult.empty();
        }
        town.setName(name);
        town.informResidents( Text.of( "Town name changed to ", name ) );

        return CommandResult.success();
    }

    @Override
    public CommandSpec getSpec() {
        return CommandSpec.builder()
                .permission("atherys.towns.commands.town.set.name")
                .description(Text.of("Used to set the name of the town."))
                .arguments(GenericArguments.remainingJoinedStrings(Text.of("newName")))
                .executor(this)
                .build();
    }
}
