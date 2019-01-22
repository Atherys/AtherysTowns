package com.atherys.towns.command.town;

import com.atherys.core.command.annotation.Aliases;
import com.atherys.core.command.annotation.Children;
import com.atherys.core.command.annotation.Description;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;

@Aliases({"town", "t"})
@Description("Base town command")
@Children({
        CreateTownCommand.class,
        RuinTownCommand.class,
        JoinTownCommand.class,
        LeaveTownCommand.class,
        DecreaseTownSizeCommand.class,
        IncreaseTownSizeCommand.class,
        ClaimPlotCommand.class,
        AbandonPlotCommand.class,
        TownInfoCommand.class,
        TownAddActorPermissionCommand.class,
        TownRemoveActorPermissionCommand.class,
        SetTownColorCommand.class,
        SetTownDescriptionCommand.class,
        SetTownMotdCommand.class,
        SetTownNameCommand.class
})
public class TownCommand implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        return null;
    }
}
