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

public class TownSetMOTDCommand extends AbstractTownSetCommand {

    TownSetMOTDCommand() {
        super(
                new String[] { "motd" },
                "motd <message>",
                Text.of( "Used to change the town MOTD." ),
                TownRank.Action.SET_MOTD
        );
    }

    @Override
    public CommandResult townsExecute(@Nullable Nation nation, @Nullable Town town, Resident resident, Player player, CommandContext args) {
        if ( town == null ) return CommandResult.empty();

        String motd = (String) args.getOne("newMOTD").orElse( town.getMOTD() );
        town.setMOTD( motd );
        town.informResidents( Text.of( "Town MOTD changed to ", town.getMOTD() ) );

        return CommandResult.success();
    }

    @Override
    public CommandSpec getSpec() {
        return CommandSpec.builder()
                .permission("atherys.towns.commands.town.set.motd")
                .description(Text.of("Used to set the MOTD of the town."))
                .arguments(GenericArguments.remainingJoinedStrings(Text.of("newMOTD")))
                .executor(this)
                .build();
    }
}
