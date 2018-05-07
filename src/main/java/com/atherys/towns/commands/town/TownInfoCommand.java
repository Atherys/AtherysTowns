package com.atherys.towns.commands.town;

import com.atherys.core.command.ParameterizedCommand;
import com.atherys.core.command.annotation.Aliases;
import com.atherys.core.command.annotation.Description;
import com.atherys.towns.commands.TownsCommand;
import com.atherys.towns.managers.TownManager;
import com.atherys.towns.messaging.TownMessage;
import com.atherys.towns.nation.Nation;
import com.atherys.towns.resident.Resident;
import com.atherys.towns.town.Town;
import java.util.Optional;
import javax.annotation.Nullable;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

@Aliases("info")
@Description("Used to get information on a town based on its name.")
public class TownInfoCommand extends TownsCommand implements ParameterizedCommand {

  @Override
  protected CommandResult execute(Player player, CommandContext args, Resident resident,
      @Nullable Town town, @Nullable Nation nation) {
    Optional<String> townName = args.getOne("townName");

    if (!townName.isPresent()) {

      if (resident.getTown().isPresent()) {
        Town t = resident.getTown().get();
        t.createView().show(player);
        return CommandResult.success();
      } else {
        TownMessage.warn(player, Text.of("You are not part of a town!"));
        return CommandResult.empty();
      }

    } else {

      Optional<Town> tOpt;
      Text error;

      if (townName.get().equalsIgnoreCase("here")) {
        error = Text.of("You are in the wilderness.");
        tOpt = TownManager.getInstance().getByLocation(player.getLocation());
      } else {
        error = Text.of("No such town exists.");
        tOpt = TownManager.getInstance().getFirstByName(townName.get());
      }

      if (tOpt.isPresent()) {
        tOpt.get().createView().show(player);
      } else {
        TownMessage.warn(player, error);
        return CommandResult.empty();
      }
    }

    return CommandResult.empty();
  }

  @Override
  public CommandElement[] getArguments() {
    return new CommandElement[]{
        GenericArguments.optional(GenericArguments.remainingJoinedStrings(Text.of("townName")))
    };
  }
}
