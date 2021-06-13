package com.atherys.towns.command.rent;

import com.atherys.core.command.ParameterizedCommand;
import com.atherys.core.command.PlayerCommand;
import com.atherys.core.command.annotation.Aliases;
import com.atherys.core.command.annotation.Description;
import com.atherys.core.command.annotation.Permission;
import com.atherys.towns.AtherysTowns;
import com.atherys.towns.api.permission.TownsPermissionContext;
import com.atherys.towns.api.permission.TownsPermissionContexts;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import javax.annotation.Nonnull;
import java.math.BigDecimal;
import java.time.Duration;

@Aliases("create")
@Permission("atherystowns.rent.create")
@Description("Makes the plot you're standing in a rented one.")
public class CreateRentCommand implements PlayerCommand, ParameterizedCommand {
    @Nonnull
    @Override
    public CommandResult execute(@Nonnull Player source, @Nonnull CommandContext args) throws CommandException {
        AtherysTowns.getInstance().getRentFacade().makePlotRentable(
                source,
                args.<BigDecimal>getOne("price").get(),
                args.<Duration>getOne("period").get(),
                args.<TownsPermissionContext>getOne("context").orElse(TownsPermissionContexts.TOWN)
        );
        return CommandResult.success();
    }

    @Override
    public CommandElement[] getArguments() {
        return new CommandElement[] {
                GenericArguments.bigDecimal(Text.of("price")),
                GenericArguments.duration(Text.of("period")),
                GenericArguments.optional(GenericArguments.catalogedElement(Text.of("context"), TownsPermissionContext.class))
        };
    }
}
