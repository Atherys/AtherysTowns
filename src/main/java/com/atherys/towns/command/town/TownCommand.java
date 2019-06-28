package com.atherys.towns.command.town;

import com.atherys.core.command.PlayerCommand;
import com.atherys.core.command.annotation.Aliases;
import com.atherys.core.command.annotation.Children;
import com.atherys.core.command.annotation.Description;
import com.atherys.towns.AtherysTowns;
import com.atherys.towns.command.town.admin.DecreaseTownSizeCommand;
import com.atherys.towns.command.town.admin.IncreaseTownSizeCommand;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
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
        SetTownNameCommand.class
})
public class TownCommand implements PlayerCommand {
    @Override
    public CommandResult execute(Player src, CommandContext args) throws CommandException {
        AtherysTowns.getInstance().getTownFacade().sendTownInfo(src);
        return CommandResult.success();
    }
}
