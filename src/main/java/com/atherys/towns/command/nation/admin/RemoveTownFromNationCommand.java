package com.atherys.towns.command.nation.admin;

import com.atherys.core.command.ParameterizedCommand;
import com.atherys.core.command.annotation.Aliases;
import com.atherys.core.command.annotation.Description;
import com.atherys.core.command.annotation.Permission;
import com.atherys.towns.AtherysTowns;
import com.atherys.towns.model.entity.Nation;
import com.atherys.towns.model.entity.Town;
import com.atherys.towns.util.TownsElements;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;


import javax.annotation.Nonnull;

@Aliases("remove")
@Description("Removes a town from a nation.")
@Permission("atherystowns.nation.admin.remove")
public class RemoveTownFromNationCommand implements ParameterizedCommand {

    @Override
    public CommandElement[] getArguments() {
        return new CommandElement[]{
                TownsElements.nation(),
                TownsElements.town()
        };
    }

    @Nonnull
    @Override
    public CommandResult execute(@Nonnull CommandSource src, CommandContext args) throws CommandException {
        AtherysTowns.getInstance().getNationFacade().removeTownFromNation(
                args.<Nation>getOne("nation").get(),
                args.<Town>getOne("town").get()
        );
        return CommandResult.success();
    }
}
