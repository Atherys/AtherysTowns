package com.atherys.towns.commands.town;

import com.atherys.towns.managers.PlotManager;
import com.atherys.towns.messaging.TownMessage;
import com.atherys.towns.nation.Nation;
import com.atherys.towns.plot.Plot;
import com.atherys.towns.resident.Resident;
import com.atherys.towns.resident.ranks.TownRank;
import com.atherys.towns.town.Town;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import javax.annotation.Nullable;
import java.util.Optional;

public class TownUnclaimCommand extends AbstractTownCommand {

    TownUnclaimCommand() {
        super(
                new String[] { "unclaim" },
                "unclaim",
                Text.of("Used to unclaim the plot ( from your own town ) you are currently standing in."),
                TownRank.Action.UNCLAIM_PLOT,
                true,
                false,
                true,
                true
        );
    }

    @Override
    public CommandResult townsExecute(@Nullable Nation nation, @Nullable Town town, Resident resident, Player player, CommandContext args) {

        if ( town == null ) {
            return CommandResult.empty();
        }

        Optional<Plot> plotOpt = PlotManager.getInstance().getByLocation(player.getLocation());

        if ( !plotOpt.isPresent() ) {
            TownMessage.warn( player, "You must be standing on a plot owned by your town in order to unclaim." );
            return CommandResult.empty();
        }

        Plot p = plotOpt.get();

        if ( !p.getParent().isPresent() || !p.getParent().get().equals(town) ) {
            TownMessage.warn(player, "You cannot unclaim the plots of another town!" );
            return CommandResult.empty();
        }

        if ( town.getPlots().size() <= 1 ) {
            TownMessage.warn( player, "You cannot unclaim your last plot!" );
            return CommandResult.empty();
        }

        town.unclaimPlot ( p );
        TownMessage.inform( player, "Plot Unclaimed." );

        return CommandResult.success();
    }

    @Override
    public CommandSpec getSpec() {
        return  CommandSpec.builder()
                .permission("atherys.towns.commands.town.unclaim")
                .description(Text.of( "Used to unclaim a plot belonging to your town." ) )
                .executor(this)
                .build();
    }
}
