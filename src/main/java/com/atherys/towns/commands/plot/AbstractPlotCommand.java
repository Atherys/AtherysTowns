package com.atherys.towns.commands.plot;

import com.atherys.towns.commands.AbstractCommand;
import com.atherys.towns.resident.ranks.TownRank;
import com.atherys.towns.resident.ranks.TownsAction;
import org.spongepowered.api.text.Text;

abstract class AbstractPlotCommand extends AbstractCommand {

    AbstractPlotCommand(String[] aliases, String syntax, Text description, boolean subcommand) {
        super(aliases, Text.of("/p ", syntax), description, TownRank.Action.NONE, false, false, false);
        if ( subcommand ) PlotMasterCommand.getInstance().getChildren().add(this);
    }

    AbstractPlotCommand(String[] aliases, String syntax, Text description) {
        super(aliases, Text.of("/p ", syntax), description, TownRank.Action.NONE, false, false, false);
        PlotMasterCommand.getInstance().getChildren().add(this);
    }

    AbstractPlotCommand(String[] aliases, String syntax, Text description, TownsAction action, boolean checkTown, boolean checkAction ) {
        super(aliases, Text.of("/p ", syntax), description, action, checkTown, false, checkAction);
        PlotMasterCommand.getInstance().getChildren().add(this);
    }

}
