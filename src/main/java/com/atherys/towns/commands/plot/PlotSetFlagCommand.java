package com.atherys.towns.commands.plot;

import com.atherys.towns.TownsConfig;
import com.atherys.towns.commands.TownsSimpleCommand;
import com.atherys.towns.managers.PlotManager;
import com.atherys.towns.messaging.TownMessage;
import com.atherys.towns.nation.Nation;
import com.atherys.towns.permissions.actions.TownActions;
import com.atherys.towns.plot.Plot;
import com.atherys.towns.plot.PlotFlags;
import com.atherys.towns.resident.Resident;
import com.atherys.towns.town.Town;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import javax.annotation.Nullable;
import java.util.Optional;

public class PlotSetFlagCommand extends TownsSimpleCommand {
    private static PlotSetFlagCommand instance = new PlotSetFlagCommand();

    public static PlotSetFlagCommand getInstance() {
        return instance;
    }

    @Override
    protected CommandResult execute(Player player, CommandContext args, Resident resident, @Nullable Town town, @Nullable Nation nation) {

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
            TownMessage.warn( player, "You must provide a valid flag. Possible flags: ", TownsConfig.PRIMARY_COLOR, PlotFlags.Flag.values() );
            return CommandResult.empty();
        }

        Optional<PlotFlags.Extent> extent = args.getOne("extent");
        if ( !extent.isPresent() ) {
            TownMessage.warn( player, "You must provide a valid extent. Possible extents: ", TownsConfig.PRIMARY_COLOR, PlotFlags.Extent.values() );
            return CommandResult.empty();
        }

        if ( player.hasPermission( flag.get().getAction().getPermission() ) ) {

            PlotFlags.Extent ext = extent.get();

            if ( !flag.get().checkExtent( ext ) ) {
                TownMessage.warn( player, "You cannot use the ", ext.getName(), " extent with the ", flag.get().name(), " flag");
                return CommandResult.empty();
            }

            plot.setFlag( flag.get(), ext );
            town.informResidents( Text.of("Flag ", flag.get().name(), " for plot ", plot.getName() ," changed to ", ext ) );

            return CommandResult.success();
        } else {
            TownMessage.warn( player, "Your town rank does not permit you to change the ", flag.get(),  " flag.");
            return CommandResult.empty();
        }
    }

    @Override
    public CommandSpec getSpec() {
        return CommandSpec.builder()
                .description( Text.of("Used to change the extent of a flag in a single plot.") )
                .executor( this )
                .arguments(
                        GenericArguments.enumValue( Text.of("flag"), PlotFlags.Flag.class ),
                        GenericArguments.enumValue( Text.of("extent"), PlotFlags.Extent.class )
                )
                .permission(TownActions.MODIFY_PLOT_FLAG.getPermission())
                .build();
    }
}
