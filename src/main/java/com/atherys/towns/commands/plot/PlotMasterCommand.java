package com.atherys.towns.commands.plot;

import com.atherys.towns.commands.TownsMasterCommand;
import com.atherys.towns.nation.Nation;
import com.atherys.towns.resident.Resident;
import com.atherys.towns.town.Town;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import javax.annotation.Nullable;

public class PlotMasterCommand extends TownsMasterCommand {

    private static PlotMasterCommand instance = new PlotMasterCommand();

    private PlotMasterCommand () {
        addChild( PlotToolCommand.getInstance().getSpec(), "tool" );
        addChild( PlotHereCommand.getInstance().getSpec(), "here" );
        addChild( PlotSetFlagCommand.getInstance().getSpec(), "setflag", "flag" );
        addChild( PlotSetNameCommand.getInstance().getSpec(), "setname", "name" );
        addChild( PlotDeselectCommand.getInstance().getSpec(), "deselect", "desel" );
    }

    public static PlotMasterCommand getInstance () {
        return instance;
    }

    @Override
    protected CommandResult execute ( Player player, CommandContext args, Resident resident, @Nullable Town town, @Nullable Nation nation ) {
        showHelp( "p", player );
        return CommandResult.empty();
    }

    @Override
    public CommandSpec getSpec () {
        return CommandSpec.builder()
                .description( Text.of( "Master Plot command." ) )
                .executor( this )
                .children( getChildren() )
                .build();
    }
}
