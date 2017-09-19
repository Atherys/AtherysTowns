package com.atherys.towns.commands.plot;

import com.atherys.towns.Settings;
import com.atherys.towns.managers.PlotManager;
import com.atherys.towns.messaging.TownMessage;
import com.atherys.towns.nation.Nation;
import com.atherys.towns.plot.Plot;
import com.atherys.towns.plot.PlotFlags;
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

public class PlotSetFlagCommand extends AbstractPlotCommand {

    PlotSetFlagCommand() {
        super(
                new String[] {"setflag", "flag"},
                "setflag <flag> <extent>",
                Text.of("Used to modify the flags of a single plot."),
                TownRank.Action.MODIFY_PLOT_FLAG,
                true,
                true
        );
    }

    @Override
    public CommandResult townsExecute(@Nullable Nation nation, @Nullable Town town, Resident resident, Player player, CommandContext args) {

        Optional<Plot> plotOpt = PlotManager.getInstance().getByLocation(player.getLocation());
        if ( !plotOpt.isPresent() ) {
            TownMessage.warn( player, "You must be standing within the borders of a town plot in order to do this command." );
            return CommandResult.empty();
        }

        Plot plot = plotOpt.get();
        if ( !plot.getTown().equals(town) ) {
            TownMessage.warn( player, "You must be standing within the borders of a plot which belongs to your own town in order to do this command." );
            return CommandResult.empty();
        }

        Optional<PlotFlags.Flag> flag = args.getOne("flag");
        if ( !flag.isPresent() ) {
            TownMessage.warn( player, "You must provide a valid flag. Possible flags: ", Settings.PRIMARY_COLOR, PlotFlags.Flag.values() );
            return CommandResult.empty();
        }

        Optional<PlotFlags.Extent> extent = args.getOne("extent");
        if ( !extent.isPresent() ) {
            TownMessage.warn( player, "You must provide a valid extent. Possible extents: ", Settings.PRIMARY_COLOR, PlotFlags.Extent.values() );
            return CommandResult.empty();
        }

        if ( resident.can ( TownRank.Action.fromFlag(flag.get() ) ) ) {

            PlotFlags.Extent ext = extent.get();

            if ( !flag.get().checkExtent( ext ) ) {
                TownMessage.warn( player, "You cannot use the ", ext.getName(), " extent with the ", flag.get().name(), " flag");
                return CommandResult.empty();
            }

            plot.setFlag( flag.get(), ext );
            town.informResidents( Text.of("Flag ", flag.get().name(), " for plot ", plot.getName() ," changed to ", ext.getName()) );

            return CommandResult.success();
        } else {
            TownMessage.warn( player, "Your town rank does not permit you to change the " + flag.get().name() + " flag.");
            return CommandResult.empty();
        }
    }

    @Override
    public CommandSpec getSpec() {
        return CommandSpec.builder()
                .permission("atherys.towns.commands.plot.setflag")
                .description(Text.of("Used to modify the flags of a plot."))
                .arguments(
                        GenericArguments.enumValue(Text.of("flag"), PlotFlags.Flag.class),
                        GenericArguments.enumValue(Text.of("extent"), PlotFlags.Extent.class)
                )
                .executor(this)
                .build();
    }
}
