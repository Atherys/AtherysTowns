package com.atherys.towns.command.plot;

import com.atherys.core.command.PlayerCommand;
import com.atherys.core.command.annotation.*;
import com.atherys.towns.AtherysTowns;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;

import javax.annotation.Nonnull;

@Aliases({"plot", "p"})
@Description("Base plot command.")
@Children({
        PlotInfoCommand.class,
        PlotSelectCommand.class,
        SetPlotNameCommand.class,
        GrantPlotCommand.class,
        BordersCommand.class
})
@Permission("atherystowns.plot.base")
@HelpCommand(title = "Plot Help", command = "help")
public class PlotCommand implements PlayerCommand {

    @Nonnull
    @Override
    public CommandResult execute(@Nonnull Player source, @Nonnull CommandContext args) throws CommandException {
        AtherysTowns.getInstance().getPlotFacade().sendInfoOnPlotAtPlayerLocation(source);
        return CommandResult.success();
    }
}
