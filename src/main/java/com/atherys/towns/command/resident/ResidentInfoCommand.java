package com.atherys.towns.command.resident;

import com.atherys.core.command.ParameterizedCommand;
import com.atherys.core.command.annotation.Aliases;
import com.atherys.core.command.annotation.Description;
import com.atherys.core.command.annotation.Permission;
import com.atherys.core.utils.UserElement;
import com.atherys.towns.AtherysTowns;
import com.atherys.towns.entity.Resident;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;

@Aliases("info")
@Description("Displays information about a resident")
@Permission("atherystowns.resident.info")
public class ResidentInfoCommand implements ParameterizedCommand {
    @Override
    public CommandElement[] getArguments() {
        return new CommandElement[]{
                new UserElement(Text.of("player"))
        };
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        AtherysTowns.getInstance().getResidentFacade().sendResidentInfo(
                src, args.<User>getOne("player").get()
        );
        return CommandResult.success();
    }
}
