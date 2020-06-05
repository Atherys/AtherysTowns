package com.atherys.towns.command.town;

import com.atherys.core.command.ParameterizedCommand;
import com.atherys.core.command.PlayerCommand;
import com.atherys.core.command.annotation.Aliases;
import com.atherys.core.command.annotation.Description;
import com.atherys.core.command.annotation.Permission;
import com.atherys.core.utils.UserElement;
import com.atherys.towns.AtherysTowns;
import com.atherys.towns.util.TownsElements;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;

import javax.annotation.Nonnull;

@Aliases({"add", "grant", "give"})
@Description("Add town role to resident")
@Permission("atherystowns.town.set.role")
public class AddTownRoleCommand implements PlayerCommand, ParameterizedCommand {
    @Override
    public CommandElement[] getArguments() {
        return new CommandElement[] {
                new UserElement(Text.of("resident")),
                TownsElements.townRole()
        };
    }

    @Nonnull
    @Override
    public CommandResult execute(@Nonnull Player source, @Nonnull CommandContext args) throws CommandException {
        AtherysTowns.getInstance().getTownFacade().addTownRole(
                source,
                args.<User>getOne("resident").get(),
                args.<String>getOne("role").get()
        );
        return CommandResult.success();
    }
}
