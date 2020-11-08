package com.atherys.towns.command.plot;

import com.atherys.core.command.ParameterizedCommand;
import com.atherys.core.command.PlayerCommand;
import com.atherys.core.command.annotation.Aliases;
import com.atherys.core.command.annotation.Description;
import com.atherys.core.command.annotation.Permission;
import com.atherys.towns.AtherysTowns;
import com.atherys.towns.api.permission.TownsPermissionContext;
import com.atherys.towns.api.permission.world.WorldPermission;
import com.atherys.towns.service.PlotService;
import com.atherys.towns.util.TownsElements;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import javax.annotation.Nonnull;

@Aliases("grant")
@Description("Grant permission to plot group")
@Permission("atherystowns.plot.permission.grant")
public class PlotPermissionGrantCommand implements ParameterizedCommand, PlayerCommand {
    @Override
    public CommandElement[] getArguments() {
        return new CommandElement[]{
                TownsElements.townPermissionContext(),
                GenericArguments.choices(
                        Text.of("permission"),
                        AtherysTowns.getInstance().getPermissionFacade().WORLD_PERMISSIONS
                )
        };
    }

    @Nonnull
    @Override
    public CommandResult execute(@Nonnull Player source, @Nonnull CommandContext args) throws CommandException {
        AtherysTowns.getInstance().getPlotFacade().addPlotPermission(
                source,
                args.<TownsPermissionContext>getOne("type").get(),
                args.<WorldPermission>getOne("permission").get()
        );
        return CommandResult.success();
    }
}
