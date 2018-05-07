package com.atherys.towns.commands.town.set;

import com.atherys.core.command.ParameterizedCommand;
import com.atherys.core.command.annotation.Aliases;
import com.atherys.core.command.annotation.Description;
import com.atherys.core.command.annotation.Permission;
import com.atherys.towns.AtherysTowns;
import com.atherys.towns.commands.TownsCommand;
import com.atherys.towns.messaging.TownMessage;
import com.atherys.towns.nation.Nation;
import com.atherys.towns.resident.Resident;
import com.atherys.towns.town.Town;
import javax.annotation.Nullable;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

@Aliases("name")
@Description("Used to change the name of the town.")
@Permission("atherystowns.town.set.name")
public class TownSetNameCommand extends TownsCommand implements ParameterizedCommand {

  @Override
  protected CommandResult execute(Player player, CommandContext args, Resident resident,
      @Nullable Town town, @Nullable Nation nation) {
    if (town == null) {
      return CommandResult.empty();
    }

    String name = (String) args.getOne("newName").orElse(town.getName());
    if (name.length() > AtherysTowns.getConfig().TOWN.MAX_NAME_LENGTH) {
      TownMessage.warn(player,
          "Town name must not exceed " + AtherysTowns.getConfig().TOWN.MAX_NAME_LENGTH
              + " symbols.");
      return CommandResult.empty();
    }
    town.setName(name);
    town.informResidents(Text.of("Town name changed to ", name));

    return CommandResult.success();
  }

  @Override
  public CommandElement[] getArguments() {
    return new CommandElement[]{
        GenericArguments.remainingJoinedStrings(Text.of("newName"))
    };
  }
}
