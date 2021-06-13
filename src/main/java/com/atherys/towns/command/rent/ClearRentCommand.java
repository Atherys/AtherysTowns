package com.atherys.towns.command.rent;

import com.atherys.core.command.PlayerCommand;
import com.atherys.core.command.annotation.Aliases;
import com.atherys.core.command.annotation.Description;
import com.atherys.core.command.annotation.Permission;
import com.atherys.towns.AtherysTowns;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;

import javax.annotation.Nonnull;

@Aliases("clear")
@Description("Clears a plot from being rented.")
@Permission("atherystowns.rent.clear")
public class ClearRentCommand implements PlayerCommand {
    @Nonnull
    @Override
    public CommandResult execute(@Nonnull Player player, @Nonnull CommandContext commandContext) throws CommandException {
        AtherysTowns.getInstance().getRentFacade().clearPlotRentable(player);
        return CommandResult.success();
    }
}
