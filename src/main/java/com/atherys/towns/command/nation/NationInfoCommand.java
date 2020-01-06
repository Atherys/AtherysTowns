package com.atherys.towns.command.nation;

import com.atherys.core.command.ParameterizedCommand;
import com.atherys.core.command.annotation.Aliases;
import com.atherys.core.command.annotation.Description;
import com.atherys.core.command.annotation.Permission;
import com.atherys.towns.AtherysTowns;
import com.atherys.towns.entity.Nation;
import com.atherys.towns.util.TownsElements;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.text.Text;

import javax.annotation.Nonnull;

@Aliases("info")
@Description("Displays information about the nation.")
@Permission("atherystowns.nation.info")
public class NationInfoCommand implements ParameterizedCommand {
    @Override
    public CommandElement[] getArguments() {
        return new CommandElement[] {
                TownsElements.nation()
        };
    }

    @Nonnull
    @Override
    public CommandResult execute(@Nonnull CommandSource src, @Nonnull CommandContext args) throws CommandException {
        AtherysTowns.getInstance().getNationFacade().sendNationInfo(src, args.<Nation>getOne("nation").get());
        return CommandResult.success();
    }
}
