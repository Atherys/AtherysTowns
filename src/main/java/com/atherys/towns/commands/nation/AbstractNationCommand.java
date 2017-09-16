package com.atherys.towns.commands.nation;

import com.atherys.towns.Settings;
import com.atherys.towns.commands.AbstractCommand;
import com.atherys.towns.resident.ranks.NationRank;
import org.spongepowered.api.text.Text;

abstract class AbstractNationCommand extends AbstractCommand{

    AbstractNationCommand(String[] aliases, String syntax, Text description, NationRank.Action action, boolean checkTown, boolean checkNation, boolean checkAction, boolean subcommand) {
        super( aliases, Text.of(Settings.DECORATION_COLOR, "/n ", syntax), description, action, checkTown, checkNation, checkAction );
        if ( subcommand ) NationMasterCommand.getInstance().getChildren().add(this);
    }

}
