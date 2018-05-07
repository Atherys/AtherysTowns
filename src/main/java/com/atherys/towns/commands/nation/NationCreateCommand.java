package com.atherys.towns.commands.nation;

import com.atherys.core.command.ParameterizedCommand;
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
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

@Aliases("create")
@Description("Used to create a new nation with your current town as its capital.")
@Permission("atherystowns.nation.create")
public class NationCreateCommand extends TownsCommand implements ParameterizedCommand {

  @Override
  protected CommandResult execute(Player player, CommandContext args, Resident resident,
      @Nullable Town town, @Nullable Nation nation) {
    if (town == null || nation != null) {
      TownMessage.warn(player,
          "You must leave your current nation and be the mayor of an independent town in order to create a new nation.");
      return CommandResult.empty();
    }

    Nation newNation = Nation
        .create(args.<String>getOne("nationName").orElse(town + "'s Nation"), town);
    newNation.createView().show(player);

    return CommandResult.success();
  }

  @Override
  public CommandElement[] getArguments() {
    return new CommandElement[]{
        GenericArguments.remainingJoinedStrings(Text.of("nationName"))
    };
  }
}
