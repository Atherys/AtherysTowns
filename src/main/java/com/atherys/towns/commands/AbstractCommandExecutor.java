package com.atherys.towns.commands;

import com.atherys.towns.nation.Nation;
import com.atherys.towns.resident.Resident;
import com.atherys.towns.town.Town;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;

import javax.annotation.Nullable;

public interface AbstractCommandExecutor {
    CommandResult townsExecute (@Nullable Nation nation, @Nullable Town town, Resident resident, Player player, CommandContext args);
}
