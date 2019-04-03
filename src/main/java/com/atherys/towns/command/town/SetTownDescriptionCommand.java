package com.atherys.towns.command.town;

import com.atherys.core.command.ParameterizedCommand;
import com.atherys.core.command.annotation.Aliases;
import com.atherys.core.command.annotation.Description;
import com.atherys.core.command.annotation.Permission;
import com.atherys.towns.AtherysTowns;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.util.Optional;

@Aliases({"describe", "description"})
@Description("Sets the description of your town.")
@Permission("atherystowns.town.describe")
public class SetTownDescriptionCommand implements ParameterizedCommand {

    @Override
    public CommandElement[] getArguments() {
        return new CommandElement[]{
                GenericArguments.remainingJoinedStrings(Text.of("description"))
        };
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (!(src instanceof Player)) {
            throw new CommandException(Text.of("Must be in-game to execute this command."));
        }
        Optional<String> description = args.getOne(Text.of("description"));
        if (!description.isPresent()) {
            throw new CommandException(Text.of("The town description cannot be empty"));
        }

        AtherysTowns.getInstance().getTownFacade().setTownDescription((Player) src, description.get());

        return CommandResult.empty();
    }
}
