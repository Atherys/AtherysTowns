package com.atherys.towns.commands.plot;

import com.atherys.towns.commands.TownsMasterCommand;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

public class PlotMasterCommand extends TownsMasterCommand {

    private static PlotMasterCommand instance = new PlotMasterCommand();

    private PlotMasterCommand() {
        addChild( PlotToolCommand.getInstance().getSpec(), "tool" );
        addChild( PlotHereCommand.getInstance().getSpec(), "here" );
        addChild( PlotSetFlagCommand.getInstance().getSpec(), "setflag", "flag" );
        addChild( PlotSetNameCommand.getInstance().getSpec(), "setname", "name" );
        addChild( PlotDeselectCommand.getInstance().getSpec(), "deselect", "desel" );
    }

    public static PlotMasterCommand getInstance() {
        return instance;
    }

    @Override
    public CommandSpec getSpec() {
        return CommandSpec.builder()
                .description( Text.of( "Master Plot command." ) )
                .executor( this )
                .children( getChildren() )
                .build();
    }
}
