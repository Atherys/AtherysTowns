package com.atherys.towns.command.nation;

import com.atherys.core.command.ParameterizedCommand;
import com.atherys.core.command.PlayerCommand;
import com.atherys.core.command.annotation.Aliases;
import com.atherys.core.command.annotation.Description;
import com.atherys.core.command.annotation.Permission;
import com.atherys.towns.AtherysTowns;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

import javax.annotation.Nonnull;

@Aliases("description")
@Description("Sets the nation's description.")
@Permission("atherystowns.nation.description")
public class SetNationDescriptionCommand implements ParameterizedCommand, PlayerCommand {
    @Override
    public CommandElement[] getArguments() {
        return new CommandElement[] {
                GenericArguments.text(
                        Text.of("description"),
                        TextSerializers.FORMATTING_CODE,
                        true
                )
        };
    }

    @Nonnull
    @Override
    public CommandResult execute(@Nonnull Player source, @Nonnull CommandContext args) throws CommandException {
        AtherysTowns.getInstance().getNationFacade().setNationDescription(
                source, args.<Text>getOne("description").get()
        );
        return CommandResult.success();
    }
}