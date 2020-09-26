package com.atherys.towns.command.plot;

import com.atherys.core.command.PlayerCommand;
import com.atherys.core.command.annotation.Aliases;
import com.atherys.core.command.annotation.Description;
import com.atherys.core.command.annotation.Permission;
import com.atherys.towns.AtherysTowns;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;

import javax.annotation.Nonnull;

@Aliases("list")
@Description("Lists all world permissions.")
@Permission("atherystowns.plot.permission.list")
public class PlotPermissionListCommand implements CommandExecutor, PlayerCommand {

    @Nonnull
    @Override
    public CommandResult execute(@Nonnull Player player, @Nonnull CommandContext commandContext) throws CommandException {
        AtherysTowns.getInstance().getPlotFacade().sendPlotPermissions(player);
        return CommandResult.success();
    }
}
