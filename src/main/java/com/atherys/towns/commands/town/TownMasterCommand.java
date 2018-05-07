package com.atherys.towns.commands.town;

import com.atherys.core.command.annotation.Aliases;
import com.atherys.core.command.annotation.Children;
import com.atherys.core.command.annotation.Description;
import com.atherys.towns.commands.TownsCommand;
import com.atherys.towns.commands.town.set.TownSetMasterCommand;
import com.atherys.towns.messaging.TownMessage;
import com.atherys.towns.nation.Nation;
import com.atherys.towns.resident.Resident;
import com.atherys.towns.town.Town;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;

import javax.annotation.Nullable;

@Aliases({"town", "t"})
@Description("The master town command.")
@Children({
        TownHelpCommand.class,
        TownHereCommand.class,
        TownInfoCommand.class,
        TownCreateCommand.class,
        TownJoinCommand.class,
        TownLeaveCommand.class,
        TownKickCommand.class,
        TownSpawnCommand.class,
        TownDepositCommand.class,
        TownWithdrawCommand.class,
        TownBorderCommand.class,
        TownClaimCommand.class,
        TownUnclaimCommand.class,
        TownInviteCommand.class,
        TownRuinCommand.class,
        TownSetMasterCommand.class
})
public class TownMasterCommand extends TownsCommand {

    @Override
    protected CommandResult execute(Player player, CommandContext args, Resident resident,
                                    @Nullable Town town, @Nullable Nation nation) {
        if (resident.getTown().isPresent()) {
            resident.getTown().get().createView().show(player);
        } else {
            TownMessage.warn(player, "You are not part of a town.");
        }
        return CommandResult.empty();
    }
}
