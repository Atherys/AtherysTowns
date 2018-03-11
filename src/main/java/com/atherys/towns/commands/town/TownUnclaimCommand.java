package com.atherys.towns.commands.town;

import com.atherys.towns.commands.TownsSimpleCommand;
import com.atherys.towns.managers.PlotManager;
import com.atherys.towns.messaging.TownMessage;
import com.atherys.towns.nation.Nation;
import com.atherys.towns.permissions.actions.TownActions;
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

public class TownUnclaimCommand extends TownsSimpleCommand {

    private static TownUnclaimCommand instance = new TownUnclaimCommand();

    public static TownUnclaimCommand getInstance () {
        return instance;
    }

    @Override
    protected CommandResult execute ( Player player, CommandContext args, Resident resident, @Nullable Town town, @Nullable Nation nation ) {
        if ( town == null ) {
            return CommandResult.empty();
        }

        Optional<Plot> plotOpt = PlotManager.getInstance().getByLocation( player.getLocation() );

        if ( !plotOpt.isPresent() ) {
            TownMessage.warn( player, "You must be standing on a plot owned by your town in order to unclaim." );
            return CommandResult.empty();
        }

        Plot p = plotOpt.get();

        if ( !p.getParent().isPresent() || !p.getParent().get().equals( town ) ) {
            TownMessage.warn( player, "You cannot unclaim the plots of another town!" );
            return CommandResult.empty();
        }

        if ( town.getPlots().size() <= 1 ) {
            TownMessage.warn( player, "You cannot unclaim your only plot!" );
            return CommandResult.empty();
        }

        town.unclaimPlot( p );
        TownMessage.inform( player, "Plot Unclaimed." );

        return CommandResult.success();
    }

    @Override
    public CommandSpec getSpec () {
        return CommandSpec.builder()
                .description( Text.of( "Used to unclaim the plot you are currently standing on." ) )
                .permission( TownActions.UNCLAIM_PLOT.getPermission() )
                .executor( this )
                .build();
    }
}
