package com.atherys.towns.commands.plot;

import com.atherys.towns.managers.PlotManager;
import com.atherys.towns.messaging.TownMessage;
import com.atherys.towns.nation.Nation;
import com.atherys.towns.plot.Plot;
import com.atherys.towns.resident.Resident;
import com.atherys.towns.town.Town;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import javax.annotation.Nullable;
import java.util.Optional;

public class PlotHereCommand extends AbstractPlotCommand {

    PlotHereCommand() {
        super(
                new String[] { "here", "get" },
                "here",
                Text.of("Used to get information on the plot you are currently standing on.")
        );
    }

    @Override
    public CommandResult townsExecute(@Nullable Nation nation, @Nullable Town town, Resident resident, Player player, CommandContext args) {
        Optional<Plot> plot = PlotManager.getInstance().getByLocation( player.getLocation());
        if ( !plot.isPresent() ) {
            TownMessage.warn(player, "You are currently standing in the wilderness.");
            return CommandResult.empty();
        }

        player.sendMessage( plot.get().getFormattedInfo() );

        return CommandResult.empty();
    }

    @Override
    public CommandSpec getSpec() {
        return CommandSpec.builder()
                .permission("atherys.towns.commands.plot.here")
                .description(Text.of("Used to get information on the plot you are currently standing in ( if any )."))
                .executor(this)
                .build();
    }
}
