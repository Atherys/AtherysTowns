package com.atherys.towns.command.plot;

import com.atherys.core.command.annotation.Aliases;
import com.atherys.core.command.annotation.Description;
import com.atherys.core.command.annotation.Permission;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;

import javax.annotation.Nonnull;

@Aliases("list")
@Description("Lists all world permissions.")
@Permission("atherystowns.plot.permission.list")
public class PlotPermissionListCommand implements CommandExecutor {

    @Nonnull
    @Override
    public CommandResult execute(@Nonnull CommandSource src, @Nonnull CommandContext args) throws CommandException {
        //Todo: Add command to list world permissions.
        return CommandResult.success();
    }
}
