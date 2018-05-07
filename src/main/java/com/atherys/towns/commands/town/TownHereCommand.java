package com.atherys.towns.commands.town;

import com.atherys.core.command.annotation.Aliases;
import com.atherys.core.command.annotation.Description;
import com.atherys.towns.commands.TownsCommand;
import com.atherys.towns.managers.PlotManager;
import com.atherys.towns.messaging.TownMessage;
import com.atherys.towns.nation.Nation;
import com.atherys.towns.plot.Plot;
import com.atherys.towns.resident.Resident;
import com.atherys.towns.town.Town;
import java.util.Optional;
import javax.annotation.Nullable;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;

@Aliases("here")
@Description("Used to get information on the town you are currently standing in.")
public class TownHereCommand extends TownsCommand {

  @Override
  protected CommandResult execute(Player player, CommandContext args, Resident resident,
      @Nullable Town town, @Nullable Nation nation) {
    Optional<Plot> pHere = PlotManager.getInstance().getByLocation(player.getLocation());
    if (pHere.isPresent()) {
      pHere.get().createView().show(player);
      return CommandResult.success();
    } else {
      TownMessage.warn(player, "You are in the wilderness.");
    }
    return CommandResult.empty();
  }
}
