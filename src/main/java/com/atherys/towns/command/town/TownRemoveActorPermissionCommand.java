package com.atherys.towns.command.town;

import com.atherys.core.command.ParameterizedCommand;
import com.atherys.core.command.PlayerCommand;
import com.atherys.core.command.annotation.Aliases;
import com.atherys.core.command.annotation.Description;
import com.atherys.core.command.annotation.Permission;
import com.atherys.towns.AtherysTowns;
import com.atherys.towns.api.permission.town.TownPermission;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;

import javax.annotation.Nonnull;

@Aliases("revoke")
@Description("Removes a permission from an entity.")
@Permission("atherystowns.town.revoke")
public class TownRemoveActorPermissionCommand implements ParameterizedCommand, PlayerCommand {
    @Override
    public CommandElement[] getArguments() {
        return new CommandElement[] {
                GenericArguments.string(Text.of("player")),
                GenericArguments.choices(
                        Text.of("permission"),
                        AtherysTowns.getInstance().getPermissionFacade().TOWN_PERMISSIONS
                )
        };
    }

    @Nonnull
    @Override
    public CommandResult execute(@Nonnull Player source, @Nonnull CommandContext args) throws CommandException {
        AtherysTowns.getInstance().getTownFacade().removeTownPermission(
                source,
                args.<String>getOne("player").get(),
                args.<TownPermission>getOne("permission").get()
        );
        return CommandResult.success();
    }
}
