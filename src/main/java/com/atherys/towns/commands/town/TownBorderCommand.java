package com.atherys.towns.commands.town;

import com.atherys.core.command.annotation.Aliases;
import com.atherys.core.command.annotation.Description;
import com.atherys.core.command.annotation.Permission;
import com.atherys.towns.commands.TownsCommand;
import com.atherys.towns.commands.TownsValues;
import com.atherys.towns.messaging.TownMessage;
import com.atherys.towns.nation.Nation;
import com.atherys.towns.resident.Resident;
import com.atherys.towns.town.Town;
import javax.annotation.Nullable;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;

@Aliases("border")
@Description("Used to toggle the town border.")
@Permission("atherystowns.town.border")
public class TownBorderCommand extends TownsCommand {

  @Override
  protected CommandResult execute(Player player, CommandContext args, Resident resident,
      @Nullable Town town, @Nullable Nation nation) {
    if (!TownsValues.get(player.getUniqueId(), TownsValues.TownsKey.TOWN_BORDERS).isPresent()) {
      TownsValues.set(player.getUniqueId(), TownsValues.TownsKey.TOWN_BORDERS, true);
      TownMessage.inform(player, "Now showing town borders.");
    } else {
      TownsValues.remove(player.getUniqueId(), TownsValues.TownsKey.TOWN_BORDERS);
      TownMessage.warn(player, "No longer showing town borders.");
    }

    return CommandResult.success();
  }
}
