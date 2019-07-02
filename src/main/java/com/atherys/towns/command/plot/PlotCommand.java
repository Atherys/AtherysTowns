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

@Aliases({"plot", "p"})
@Description("Base plot command.")
@Children({
        PlotInfoCommand.class,
        PlotSelectCommand.class,
        SetPlotNameCommand.class,
        PlotAddActorPermissionCommand.class,
        PlotRemoveActorPermissionCommand.class
})
@HelpCommand(title = "Plot Help")
public class PlotCommand implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        return CommandResult.empty();
    }
}
