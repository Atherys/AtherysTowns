package com.atherys.towns.command.plot;

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
import java.util.Optional;

@Aliases("rename")
@Permission("atherystowns.plot.rename")
@Description("Renames the plot at your location.")
public class SetPlotNameCommand implements ParameterizedCommand, PlayerCommand {
    @Override
    public CommandElement[] getArguments() {
        return new CommandElement[]{
                GenericArguments.text(Text.of("name"), TextSerializers.FORMATTING_CODE, true)
        };
    }

    @Nonnull
    @Override
    public CommandResult execute(@Nonnull Player source, @Nonnull CommandContext args) throws CommandException {
        Optional<Text> newName = args.getOne("name");
        AtherysTowns.getInstance().getPlotFacade().renameTownPlotAtPlayerLocation(source, newName.orElse(Text.EMPTY));
        return CommandResult.success();
    }
}
