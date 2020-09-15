package com.atherys.towns.command.plot;

import com.atherys.core.command.annotation.Aliases;
import com.atherys.core.command.annotation.Children;
import com.atherys.core.command.annotation.Description;
import com.atherys.core.command.annotation.HelpCommand;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;

@Aliases("select")
@Description("Base plot selection command.")
@Children({
        PlotSelectPointACommand.class,
        PlotSelectPointBCommand.class,
        PlotClearSelectionCommand.class
})

@HelpCommand(title = "Selection Help", prefix = "plot")
public class PlotSelectCommand implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        return CommandResult.empty();
    }
}
