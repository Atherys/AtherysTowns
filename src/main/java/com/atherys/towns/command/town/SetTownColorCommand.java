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
import org.spongepowered.api.text.format.TextColor;

import java.util.Optional;

@Aliases("color")
@Description("Sets the color of your town.")
@Permission("atherystowns.town.color")
public class SetTownColorCommand implements ParameterizedCommand {
    @Override
    public CommandElement[] getArguments() {
        return new CommandElement[]{
                GenericArguments.catalogedElement(Text.of("color"), TextColor.class)
        };
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (!(src instanceof Player)) {
            throw new CommandException(Text.of("Must be in-game to execute this command."));
        }
        Optional<TextColor> description = args.getOne(Text.of("color"));
        if (!description.isPresent()) {
            throw new CommandException(Text.of("The town color cannot be empty"));
        }

        AtherysTowns.getInstance().getTownFacade().setTownColor((Player) src, description.get());
        return CommandResult.empty();
    }
}
