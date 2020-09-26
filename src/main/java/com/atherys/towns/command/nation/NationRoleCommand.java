package com.atherys.towns.command.nation;

import com.atherys.core.command.annotation.*;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;

@Aliases("role")
@Description("Commands for nation roles")
@Permission("atherystowns.nation.role")
@Children({
        AddNationRoleCommand.class,
        RevokeNationRoleCommand.class
})
@HelpCommand(title = "Nation Role Help", prefix = "nation")
public class NationRoleCommand implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        return CommandResult.success();
    }
}
