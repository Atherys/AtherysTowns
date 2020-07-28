package com.atherys.towns.command.resident;

import com.atherys.core.command.PlayerCommand;
import com.atherys.core.command.annotation.*;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;

import javax.annotation.Nonnull;

@Aliases({"resident", "res"})
@Description("Base resident command.")
@Children({
        AddFriendCommand.class,
        RemoveFriendCommand.class,
        ResidentInfoCommand.class
})

@Permission("atherystowns.resident.base")
@HelpCommand(title = "Resident Help")
public class ResidentCommand implements PlayerCommand {

    @Nonnull
    @Override
    public CommandResult execute(@Nonnull Player source, @Nonnull CommandContext args) throws CommandException {
        return CommandResult.success();
    }
}
