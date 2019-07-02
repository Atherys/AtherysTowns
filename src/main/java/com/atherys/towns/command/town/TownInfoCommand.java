package com.atherys.towns.command.town;

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

@Aliases("info")
@Description("Displays information about a town.")
@Permission("atherystowns.town.info")
public class TownInfoCommand implements PlayerCommand, ParameterizedCommand {
    @Override
    public CommandElement[] getArguments() {
        return new CommandElement[]{
                GenericArguments.text(Text.of("town"), TextSerializers.FORMATTING_CODE, true)
        };
    }

    @Override
    public CommandResult execute(Player player, CommandContext args) throws CommandException {
        AtherysTowns.getInstance().getTownFacade().sendTownInfo(player, args.<Text>getOne("town").orElse(Text.EMPTY));
        return CommandResult.success();
    }
}
