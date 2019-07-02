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
import java.util.HashMap;
import java.util.Map;

@Aliases("revoke")
@Description("Removes a permission from an entity.")
@Permission("atherystowns.town.revoke")
public class TownRemoveActorPermissionCommand implements ParameterizedCommand, PlayerCommand {
    private static final Map<String, TownPermission> TOWN_PERMISSIONS = new HashMap<>();
    static {
        AtherysTowns.getInstance().getPermissionFacade().getTownPermissions()
                .forEach(permission -> TOWN_PERMISSIONS.put(permission.getId(), permission));
    }

    @Override
    public CommandElement[] getArguments() {
        return new CommandElement[] {
                GenericArguments.player(Text.of("player")),
                GenericArguments.choices(Text.of("permission"), TOWN_PERMISSIONS)
        };
    }

    @Nonnull
    @Override
    public CommandResult execute(@Nonnull Player source, @Nonnull CommandContext args) throws CommandException {
        AtherysTowns.getInstance().getTownFacade().removeTownPermission(
                source,
                args.<User>getOne("player").get(),
                args.<TownPermission>getOne("permission").get()
        );
        return CommandResult.success();
    }
}
