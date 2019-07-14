package com.atherys.towns.command.town;

import com.atherys.core.command.annotation.*;
import com.atherys.towns.AtherysTowns;
import com.atherys.towns.command.town.admin.DecreaseTownSizeCommand;
import com.atherys.towns.command.town.admin.IncreaseTownSizeCommand;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;

@Aliases({"town", "t"})
@Description("Base town command")
@Children({
        CreateTownCommand.class,
        RuinTownCommand.class,
        JoinTownCommand.class,
        LeaveTownCommand.class,
        DecreaseTownSizeCommand.class,
        IncreaseTownSizeCommand.class,
        ClaimPlotCommand.class,
        AbandonPlotCommand.class,
        TownInfoCommand.class,
        TownAddActorPermissionCommand.class,
        TownRemoveActorPermissionCommand.class,
        SetTownColorCommand.class,
        SetTownDescriptionCommand.class,
        SetTownMotdCommand.class,
        SetTownNameCommand.class,
        SetTownJoinableCommand.class,
        InviteToTownCommand.class,
        JoinTownCommand.class
})
@Permission("atherystowns.town")
@HelpCommand(
        title = "Town Help",
        command = "help"
)
public class TownCommand implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (src instanceof Player) {
            AtherysTowns.getInstance().getTownFacade().sendTownInfo((Player) src);
        }
        return CommandResult.success();
    }
}
