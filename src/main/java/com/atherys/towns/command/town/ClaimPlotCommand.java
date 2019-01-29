package com.atherys.towns.command.town;

import com.atherys.core.command.PlayerCommand;
import com.atherys.core.command.annotation.Aliases;
import com.atherys.core.command.annotation.Description;
import com.atherys.core.command.annotation.Permission;
import com.atherys.towns.AtherysTowns;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;

@Aliases("claim")
@Description("Claims a plot in the town.")
@Permission("atherystowns.town.claim")
public class ClaimPlotCommand implements PlayerCommand {
    @Override
    public CommandResult execute(Player source, CommandContext args) throws CommandException {
        AtherysTowns.getInstance().getTownFacade().claimTownPlotFromPlayerSelection(source);
        return CommandResult.success();
    }
}
