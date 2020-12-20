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
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;

@Aliases("overrideLeader")
@Permission("atherystowns.admin.town.mayor")
@Description("Set the mayor of the town. If no resident is provided, the town will become mayorless.")
public class OverrideLeaderCommand implements ParameterizedCommand {
    @Override
    public CommandElement[] getArguments() {
        return new CommandElement[] {
                TownsElements.town(),
                GenericArguments.optional(GenericArguments.user(Text.of("resident")))
        };
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        AtherysTowns.getInstance().getTownFacade().overrideLeader(
                args.<Town>getOne("town").orElse(null),
                args.<User>getOne("resident").orElse(null)
        );

        return CommandResult.success();
    }
}
