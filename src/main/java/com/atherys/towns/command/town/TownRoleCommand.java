package com.atherys.towns.command.town;

import com.atherys.core.command.annotation.*;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;

@Aliases("role")
@Description("Commands for town roles")
@Permission("atherystowns.town.role")
@Children({
        AddTownRoleCommand.class,
        RevokeTownRoleCommand.class
})
@HelpCommand(title = "Town Role Help", prefix = "town")
public class TownRoleCommand implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        return CommandResult.success();
    }
}
