package com.atherys.towns.command.town.admin;

import com.atherys.core.command.annotation.Aliases;
import com.atherys.core.command.annotation.Permission;
import com.atherys.towns.AtherysTowns;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;

@Aliases("recalculateTownSizes")
@Permission("atherystowns.admin.town.recalculateTownSizes")
public class RecalculateTownSizesCommand implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        AtherysTowns.getInstance().getTownFacade().recalculateTownSizes();
        return CommandResult.success();
    }
}
