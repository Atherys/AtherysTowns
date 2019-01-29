package com.atherys.towns.command.plot;

import com.atherys.core.command.PlayerCommand;
import com.atherys.core.command.annotation.Aliases;
import com.atherys.towns.AtherysTowns;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;

@Aliases("clear")
public class PlotClearSelectionCommand implements PlayerCommand {
    @Override
    public CommandResult execute(Player source, CommandContext args) throws CommandException {
        AtherysTowns.getInstance().getPlotSelectionFacade().clearSelection(source);
        return CommandResult.success();
    }
}
