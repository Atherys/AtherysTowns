package com.atherys.towns.command.nation.admin;

import com.atherys.core.command.ParameterizedCommand;
import com.atherys.core.command.annotation.Aliases;
import com.atherys.core.command.annotation.Description;
import com.atherys.core.command.annotation.Permission;
import com.atherys.towns.AtherysTowns;
import com.atherys.towns.model.entity.Nation;
import com.atherys.towns.util.TownsElements;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;

import javax.annotation.Nonnull;

@Aliases("disband")
@Description("Disbands a nation.")
@Permission("atherystowns.nation.admin.disband")
public class DisbandNationCommand implements ParameterizedCommand {
    @Override
    public CommandElement[] getArguments() {
        return new CommandElement[]{
                TownsElements.nation()
        };
    }

    @Nonnull
    @Override
    public CommandResult execute(@Nonnull CommandSource src, CommandContext args) throws CommandException {
        AtherysTowns.getInstance().getNationFacade().disbandNation(
                src,
                args.<Nation>getOne("nation").get()
        );
        return CommandResult.success();
    }
}