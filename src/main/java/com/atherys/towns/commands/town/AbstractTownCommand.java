package com.atherys.towns.commands.town;

import com.atherys.towns.Settings;
import com.atherys.towns.commands.AbstractCommand;
import com.atherys.towns.resident.ranks.TownRank;
import org.spongepowered.api.text.Text;

public abstract class AbstractTownCommand extends AbstractCommand {

    protected AbstractTownCommand(String[] aliases, String syntax, Text description, TownRank.Action action, boolean checkTown, boolean checkNation, boolean checkAction, boolean subcommand) {
        super(aliases, Text.of(Settings.PRIMARY_COLOR, "/t ", syntax), description, action, checkTown, checkNation, checkAction);
        if ( subcommand ) {
            TownMasterCommand.getInstance().getChildren().add(this);
        }
    }

}
