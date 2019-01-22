package com.atherys.towns.command.plot;

import com.atherys.core.command.UserCommand;
import com.atherys.core.command.annotation.Aliases;
import com.atherys.core.command.annotation.Children;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.User;

import javax.annotation.Nonnull;

@Aliases("select")
@Children({
        PlotSelectPointACommand.class,
        PlotSelectPointBCommand.class,
        PlotClearSelectionCommand.class
})
public class PlotSelectCommand implements UserCommand {
    @Nonnull
    @Override
    public CommandResult execute(@Nonnull User source, @Nonnull CommandContext args) throws CommandException {
        return null;
    }
}
