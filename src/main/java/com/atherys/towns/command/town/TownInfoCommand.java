package com.atherys.towns.command.town;

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

import javax.annotation.Nonnull;

@Aliases("info")
@Description("Displays information about a town.")
@Permission("atherystowns.town.info")
public class TownInfoCommand implements ParameterizedCommand {
    @Override
    public CommandElement[] getArguments() {
        return new CommandElement[]{
                TownsElements.town()
        };
    }

    @Nonnull
    @Override
    public CommandResult execute(@Nonnull CommandSource src, @Nonnull CommandContext args) throws CommandException {
        AtherysTowns.getInstance().getTownFacade().sendTownInfo(args.<Town>getOne("town").get(), src);
        return CommandResult.success();
    }
}
