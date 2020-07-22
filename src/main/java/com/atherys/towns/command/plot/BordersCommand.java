package com.atherys.towns.command.plot;

import com.atherys.core.command.ParameterizedCommand;
import com.atherys.core.command.PlayerCommand;
import com.atherys.core.command.annotation.Aliases;
import com.atherys.core.command.annotation.Description;
import com.atherys.core.command.annotation.Permission;
import com.atherys.towns.AtherysTowns;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import javax.annotation.Nonnull;

@Aliases("border")
@Description("Displays outline for town plots.")
@Permission("atherystowns.plot.border")
public class BordersCommand implements PlayerCommand, ParameterizedCommand {

    @Override
    public CommandElement[] getArguments() {
        return new CommandElement[]{
                GenericArguments.bool(Text.of("border"))
        };
    }

    @Nonnull
    @Override
    public CommandResult execute(@Nonnull Player source, @Nonnull CommandContext args) throws CommandException {
        AtherysTowns.getInstance().getPlotBorderFacade().setPlayerViewBorderStatus(source, args.<Boolean>getOne("border").orElse(false));
        return CommandResult.success();
    }
}
