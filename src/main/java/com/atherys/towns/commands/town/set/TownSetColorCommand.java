package com.atherys.towns.commands.town.set;

import com.atherys.core.command.ParameterizedCommand;
import com.atherys.core.command.annotation.Aliases;
import com.atherys.core.command.annotation.Description;
import com.atherys.core.command.annotation.Permission;
import com.atherys.towns.commands.TownsCommand;
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
import org.spongepowered.api.text.format.TextColor;

@Aliases("color")
@Description("Used to change the color of the town")
@Permission("atherystowns.town.set.color")
public class TownSetColorCommand extends TownsCommand implements ParameterizedCommand {

  @Override
  protected CommandResult execute(Player player, CommandContext args, Resident resident,
      @Nullable Town town, @Nullable Nation nation) {
    if (town == null) {
      return CommandResult.empty();
    }

    TextColor color = (TextColor) args.getOne("newColor").orElse(town.getColor());
    town.setColor(color);
    town.informResidents(Text.of("Town Color changed to ", town.getColor(),
        town.getColor().getName().replace('_', ' ')));

    return CommandResult.success();
  }

  @Override
  public CommandElement[] getArguments() {
    return new CommandElement[]{
        GenericArguments.catalogedElement(Text.of("newColor"), TextColor.class)
    };
  }
}
