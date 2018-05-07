package com.atherys.towns.commands.town;

import com.atherys.core.command.annotation.Aliases;
import com.atherys.core.command.annotation.Description;
import com.atherys.core.command.annotation.Permission;
import com.atherys.core.utils.Question;
import com.atherys.towns.commands.TownsCommand;
import com.atherys.towns.messaging.TownMessage;
import com.atherys.towns.nation.Nation;
import com.atherys.towns.permissions.ranks.TownRanks;
import com.atherys.towns.resident.Resident;
import com.atherys.towns.town.Town;
import com.atherys.towns.town.TownStatus;
import javax.annotation.Nullable;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

@Aliases({"ruin", "destroy"})
@Description("Used to ruin a town. This will remove the town from the game, unclaim all it's plots and leave all residents homeless.")
@Permission("atherystowns.town.ruin")
public class TownRuinCommand extends TownsCommand {

  @Override
  protected CommandResult execute(Player player, CommandContext args, Resident resident,
      @Nullable Town town, @Nullable Nation nation) {
    if (town == null) {
      return CommandResult.empty();
    }
    if (resident.getTownRank().equals(TownRanks.MAYOR) || player
        .hasPermission("atherystowns.admin.town.ruin")) {
      ruinTown(player, town);
    }
    return CommandResult.success();
  }

  private void ruinTown(Player player, Town town) {
    Question ruin = Question.of(Text
        .of("Are you sure you want to destroy your town? Doing so will eject all residents and unclaim all plots."))
        .addAnswer(Question.Answer.of(Text.of(TextColors.GREEN, "Yes"), player1 -> {
          if (town.getStatus().equals(TownStatus.CAPITAL)) {
            TownMessage.warn(player, "You cannot destroy the capital of a nation!");
          } else {
            town.ruin();
          }
        }))
        .addAnswer(Question.Answer.of(Text.of(TextColors.RED, "No"), player1 -> {
        }))
        .build();
    ruin.pollBook(player);
  }
}
