package com.atherys.towns.commands.town.set;

import com.atherys.core.command.annotation.Aliases;
import com.atherys.core.command.annotation.Description;
import com.atherys.core.command.annotation.Permission;
import com.atherys.towns.commands.TownsCommand;
import com.atherys.towns.messaging.TownMessage;
import com.atherys.towns.nation.Nation;
import com.atherys.towns.resident.Resident;
import com.atherys.towns.town.Town;
import javax.annotation.Nullable;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

@Aliases({"spawn", "home"})
@Description("Used to change the town spawn to the position the executor is currently standing in")
@Permission("atherystowns.town.set.spawn")
public class TownSetSpawnCommand extends TownsCommand {

  @Override
  protected CommandResult execute(Player player, CommandContext args, Resident resident,
      @Nullable Town town, @Nullable Nation nation) {
    if (town == null) {
      return CommandResult.empty();
    }

    Location<World> loc = player.getLocation();

    if (town.contains(loc)) {
      town.setSpawn(loc);
      town.informResidents(
          Text.of("Town Spawn changed to ", loc.getBlockPosition().toString()));
    } else {
      TownMessage.warn(player, "Town Spawn must be located within the borders of the town.");
    }

    return CommandResult.success();
  }
}
