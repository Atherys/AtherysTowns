package com.atherys.towns.command.plot;

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

@Aliases("revoke")
@Permission("atherystowns.plot.revoke")
@Description("Revokes ownership of the plot you are standing on.")
public class RevokePlotCommand implements PlayerCommand {
    @Nonnull
    @Override
    public CommandResult execute(@Nonnull Player source, @Nonnull CommandContext args) throws CommandException {
        AtherysTowns.getInstance().getPlotFacade().revokePlotAtPlayerLocation(source);
        return CommandResult.success();
    }
}
