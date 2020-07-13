package com.atherys.towns.command.town;

import com.atherys.core.command.annotation.Aliases;
import com.atherys.core.command.annotation.Children;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;

import javax.annotation.Nonnull;

@Aliases("raid")
@Children({
        TownRaidCreateCommand.class,
        TownRaidInfoCommand.class
})
public class TownRaidCommand implements CommandExecutor {

    @Nonnull
    @Override
    public CommandResult execute(@Nonnull CommandSource src, @Nonnull CommandContext args) throws CommandException {
        return CommandResult.empty();
    }
}
