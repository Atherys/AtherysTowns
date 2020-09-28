package com.atherys.towns.command.nation.admin;

import com.atherys.core.command.ParameterizedCommand;
import com.atherys.core.command.PlayerCommand;
import com.atherys.core.command.annotation.Aliases;
import com.atherys.core.command.annotation.Description;
import com.atherys.core.command.annotation.Permission;
import com.atherys.towns.AtherysTowns;
import com.atherys.towns.model.entity.Nation;
import com.atherys.towns.util.TownsElements;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.entity.living.player.Player;

import javax.annotation.Nonnull;

@Aliases("claim")
@Description("Claims the current plot selection for the specified nation.")
@Permission("atherystowns.nation.admin.plot.claim")
public class ClaimNationPlotCommand implements PlayerCommand, ParameterizedCommand {

    @Override
    public CommandElement[] getArguments() {
        return new CommandElement[]{TownsElements.nation()};
    }

    @Nonnull
    @Override
    public CommandResult execute(@Nonnull Player source, @Nonnull CommandContext args) throws CommandException {
        AtherysTowns.getInstance().getNationFacade().claimNationPlotFromPlayerSelection(source, args.<Nation>getOne("nation").get());
        return CommandResult.success();
    }
}
