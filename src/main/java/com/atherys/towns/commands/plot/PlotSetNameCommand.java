package com.atherys.towns.commands.plot;

import com.atherys.towns.AtherysTowns;
import com.atherys.towns.Settings;
import com.atherys.towns.messaging.TownMessage;
import com.atherys.towns.nation.Nation;
import com.atherys.towns.plot.Plot;
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
import java.util.Optional;

public class PlotSetNameCommand extends AbstractPlotCommand {

    PlotSetNameCommand() {
        super(
                new String[] {"setname", "name"},
                "setname <name>",
                Text.of("Used to change the name of a plot."),
                TownRank.Action.MODIFY_PLOT_NAME,
                true,
                true
        );
    }

    @Override
    public CommandResult townsExecute(@Nullable Nation nation, @Nullable Town town, Resident resident, Player player, CommandContext args) {
        Optional<Plot> plotOpt = AtherysTowns.getInstance().getPlotManager().getByLocation(player.getLocation());
        if ( !plotOpt.isPresent() ) {
            TownMessage.warn( player, "You must be standing within the borders of a town plot in order to do this command." );
            return CommandResult.empty();
        }

        Plot plot = plotOpt.get();
        if ( !plot.getParent().get().equals(town) ) {
            TownMessage.warn( player, "You must be standing within the borders of a plot which belongs to your own town in order to do this command." );
            return CommandResult.empty();
        }

        Optional<String> name = args.getOne("name");
        if ( !name.isPresent() || name.get().length() > Settings.MAX_TOWN_NAME_LENGTH ) {
            TownMessage.warn( player, "You must provide a valid name no longer than ", Settings.MAX_TOWN_NAME_LENGTH, " symbols." );
            return CommandResult.empty();
        }

        town.informResidents(Text.of("Plot ", plot.getName(), " is being renamed to ", name.get()));
        plot.setName(name.get());

        return CommandResult.success();
    }

    @Override
    public CommandSpec getSpec() {
        return CommandSpec.builder()
                .permission("atherys.towns.commands.plot.setname")
                .description(Text.of("Used to modify the name of a plot."))
                .arguments(
                        GenericArguments.remainingJoinedStrings(Text.of("name"))
                )
                .executor(this)
                .build();
    }
}
