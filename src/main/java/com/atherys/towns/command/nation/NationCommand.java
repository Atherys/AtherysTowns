package com.atherys.towns.command.nation;

import com.atherys.core.command.annotation.Aliases;
import com.atherys.core.command.annotation.Children;
import com.atherys.core.command.annotation.Description;
import com.atherys.core.command.annotation.Permission;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;

@Aliases("nation")
@Description("Base nation command.")
@Permission("atherystowns.nation")
@Children({
        CreateNationCommand.class,
        NationAddActorPermissionCommand.class,
        NationInfoCommand.class,
        NationRemoveActorPermissionCommand.class,
        SetNationDescriptionCommand.class,
        SetNationNameCommand.class
})
public class NationCommand implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        return CommandResult.empty();
    }
}
