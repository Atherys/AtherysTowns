package com.atherys.towns.commands.town.set;

import com.atherys.core.command.ParameterizedCommand;
import com.atherys.core.command.annotation.Aliases;
import com.atherys.core.command.annotation.Description;
import com.atherys.core.command.annotation.Permission;
import com.atherys.towns.commands.TownsCommand;
import com.atherys.towns.managers.NationManager;
import com.atherys.towns.messaging.TownMessage;
import com.atherys.towns.nation.Nation;
import com.atherys.towns.resident.Resident;
import com.atherys.towns.town.Town;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

@Aliases("nation")
@Description("Used to change the nation this town belongs to")
@Permission("atherystowns.town.set.nation")
public class TownSetNationCommand extends TownsCommand implements ParameterizedCommand {

  @Override
  protected CommandResult execute(Player player, CommandContext args, Resident resident,
      @Nullable Town town, @Nullable Nation nation) {
    if (town == null) {
      return CommandResult.empty();
    }

    Optional<Nation> n = NationManager.getInstance()
        .getFirstByName(args.<String>getOne("nation").orElse(UUID.randomUUID().toString()));

    if (n.isPresent()) {
      town.setParent(n.get());
      TownMessage.informAll(
          Text.of(town.getName() + " has joined the nation of " + n.get().getName()));
    } else {
      TownMessage.warn(player, "You must provide a valid nation to join.");
    }

    return CommandResult.empty();
  }

  @Override
  public CommandElement[] getArguments() {
    return new CommandElement[]{
        GenericArguments.remainingJoinedStrings(Text.of("nation"))
    };
  }
}
