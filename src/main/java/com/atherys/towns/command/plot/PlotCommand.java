package com.atherys.towns.command.plot;

import com.atherys.core.command.annotation.*;
import com.atherys.towns.AtherysTowns;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;

@Aliases({"plot", "p"})
@Description("Base plot command.")
@Children({
        PlotInfoCommand.class,
        PlotSelectCommand.class,
        SetPlotNameCommand.class,
        GrantPlotCommand.class
})
@Permission("atherystowns.plot.base")
@HelpCommand(title = "Plot Help", command = "help")
public class PlotCommand implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (src instanceof Player) {
            AtherysTowns.getInstance().getPlotFacade().sendInfoOnPlotAtPlayerLocation((Player) src);
        }
        return CommandResult.empty();
    }
}
