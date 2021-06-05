package com.atherys.towns.command.nation.admin;

import com.atherys.core.command.PlayerCommand;
import com.atherys.core.command.annotation.Aliases;
import com.atherys.core.command.annotation.Description;
import com.atherys.core.command.annotation.Permission;
import com.atherys.towns.AtherysTowns;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;

import javax.annotation.Nonnull;

@Aliases({"abandon", "unclaim"})
@Description("Deletes the nation plot you are currently standing in.")
@Permission("atherystowns.nation.admin.plot.unclaim")
public class UnclaimNationPlotCommand implements PlayerCommand {

    @Nonnull
    @Override
    public CommandResult execute(@Nonnull Player source, @Nonnull CommandContext args) throws CommandException {
        AtherysTowns.getInstance().getNationFacade().removeNationPlotAtPlayerLocation(source);
        return CommandResult.success();
    }
}
