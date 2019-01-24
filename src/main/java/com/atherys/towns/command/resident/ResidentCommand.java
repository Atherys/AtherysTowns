package com.atherys.towns.command.resident;

import com.atherys.core.command.annotation.Aliases;
import com.atherys.core.command.annotation.Children;
import com.atherys.core.command.annotation.Description;
import com.atherys.core.command.annotation.Permission;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;

@Aliases({"resident", "res"})
@Description("Base resident command.")
@Children({
        AddFriendCommand.class,
        RemoveFriendCommand.class,
        ResidentInfoCommand.class
})
@Permission("atherystowns.resident")
public class ResidentCommand implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        return CommandResult.empty();
    }
}
