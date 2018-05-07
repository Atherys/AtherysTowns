package com.atherys.towns.commands.town.set;

import com.atherys.core.command.annotation.Aliases;
import com.atherys.core.command.annotation.Children;
import com.atherys.core.command.annotation.Description;
import com.atherys.towns.commands.TownsCommand;
import com.atherys.towns.nation.Nation;
import com.atherys.towns.resident.Resident;
import com.atherys.towns.town.Town;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import javax.annotation.Nullable;

@Aliases("set")
@Description("Master command for changing properties of a town")
@Children({
        TownSetColorCommand.class,
        TownSetDescriptionCommand.class,
        TownSetNameCommand.class,
        TownSetFlagCommand.class,
        TownSetMayorCommand.class,
        TownSetMOTDCommand.class,
        TownSetRankCommand.class,
        TownSetNationCommand.class,
        TownSetSpawnCommand.class
})
public class TownSetMasterCommand extends TownsCommand {

    @Override
    protected CommandResult execute(Player player, CommandContext args, Resident resident,
                                    @Nullable Town town, @Nullable Nation nation) throws CommandException {
        throw new CommandException(Text.of("Empty '/t set' command!"));
    }
}
