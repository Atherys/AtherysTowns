package com.atherys.towns.commands.nation;

import com.atherys.core.command.ParameterizedCommand;
import com.atherys.core.command.annotation.Aliases;
import com.atherys.core.command.annotation.Description;
import com.atherys.towns.commands.TownsCommand;
import com.atherys.towns.managers.NationManager;
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
@Description("Used to get info on a nation")
public class NationInfoCommand extends TownsCommand implements ParameterizedCommand {

  @Override
  protected CommandResult execute(Player player, CommandContext args, Resident resident,
      @Nullable Town town, @Nullable Nation nation) {

    String nationName = args.<String>getOne("nation").orElseGet(() -> {
      Optional<Nation> residentNation = resident.getNation();
      if (residentNation.isPresent()) {
        return residentNation.get().getName();
      }
      return "None";
    });

    if (nationName.equals("None")) {
      return CommandResult.success();
    }

    Optional<Nation> nationOptional = NationManager.getInstance().getFirstByName(nationName);

    if (nationOptional.isPresent()) {
      nationOptional.get().createView().show(player);
    } else {
      TownMessage.warn(player, "No nation found.");
    }

    return CommandResult.success();
  }

  @Override
  public CommandElement[] getArguments() {
    return new CommandElement[]{
        GenericArguments
            .optional(GenericArguments.remainingJoinedStrings(Text.of("nation")))
    };
  }
}
