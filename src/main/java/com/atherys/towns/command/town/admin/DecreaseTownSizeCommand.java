package com.atherys.towns.command.town.admin;

import com.atherys.core.command.ParameterizedCommand;
import com.atherys.core.command.annotation.Aliases;
import com.atherys.core.command.annotation.Description;
import com.atherys.core.command.annotation.Permission;
import com.atherys.towns.AtherysTowns;
import com.atherys.towns.model.entity.Town;
import com.atherys.towns.util.TownsElements;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.text.Text;

@Aliases("decrease")
@Description("Decreases the size of the town.")
@Permission("atherystowns.admin.town.decrease")
public class DecreaseTownSizeCommand implements ParameterizedCommand {
    @Override
    public CommandElement[] getArguments() {
        return new CommandElement[]{
                TownsElements.town(),
                GenericArguments.integer(Text.of("amount"))
        };
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        AtherysTowns.getInstance().getTownAdminFacade().decreaseTownSize(
                src, args.<Town>getOne("town").get(), args.<Integer>getOne("amount").get()
        );
        return CommandResult.success();
    }
}
