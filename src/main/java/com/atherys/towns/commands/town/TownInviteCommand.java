package com.atherys.towns.commands.town;

import com.atherys.core.command.ParameterizedCommand;
import com.atherys.core.command.annotation.Aliases;
import com.atherys.core.command.annotation.Description;
import com.atherys.core.command.annotation.Permission;
import com.atherys.core.utils.Question;
import com.atherys.towns.AtherysTowns;
import com.atherys.towns.commands.TownsCommand;
import com.atherys.towns.managers.ResidentManager;
import com.atherys.towns.messaging.TownMessage;
import com.atherys.towns.nation.Nation;
import com.atherys.towns.permissions.ranks.TownRanks;
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
import org.spongepowered.api.text.format.TextColors;

@Aliases("invite")
@Description("Used to invite a resident to the town.")
@Permission("atherystowns.town.invite")
public class TownInviteCommand extends TownsCommand implements ParameterizedCommand {

  public static void inviteResident(Resident resident, Town town) {
    Optional<Player> p = resident.getPlayer();

    if (p.isPresent()) {
      Text question;
      if (town.getParent().isPresent()) {
        question = Text.of("You have been invited to the town of ", town.getName(), " in ",
            town.getParent().get().getName());
      } else {
        question = Text.of("You have been invited to the town of ", town.getName());
      }

      Question invite = Question.of(question)
          .addAnswer(Question.Answer.of(Text.of(TextColors.GREEN, "Accept"), player -> {
            if (resident.getTown().isPresent()) {
              TownMessage.warn(player, Text.of(
                  "You cannot join a town while you are part of another! Please leave your current town first."));
              return;
            }
            resident.setTown(town, TownRanks.RESIDENT);
            town.informResidents(Text.of(player.getName(), " has joined the town."));
          }))
          .addAnswer(Question.Answer.of(Text.of(TextColors.RED, "Reject"), player -> {
            town.informResidents(
                Text.of(player.getName(), " has rejected the invitation to the town."));
          }))
          .build();

      invite.pollViewButton(p.get(),
          Text.of(TownMessage.MSG_PREFIX, AtherysTowns.getConfig().COLORS.TERTIARY,
              "You have been invited to join the town of ", town.getName(),
              ". Click here to respond."));
    }
  }

  @Override
  protected CommandResult execute(Player player, CommandContext args, Resident resident,
      @Nullable Town town, @Nullable Nation nation) {
    if (town == null) {
      return CommandResult.empty();
    }

    Optional<Player> invitedPlayer = args.getOne("player");
    if (!invitedPlayer.isPresent()) {
      TownMessage.warn(player,
          "You must provide a valid ( online ) player whom to invite to your town.");
      return CommandResult.empty();
    }

    Player p = invitedPlayer.get();
    Optional<Resident> otherRes = ResidentManager.getInstance().get(p.getUniqueId());

    if (otherRes.isPresent()) {

      if (!resident.equals(otherRes.get())) {
        town.informResidents(
            Text.of(player.getName(), " has invited ", p.getName(), " to the town."));
        inviteResident(otherRes.get(), town);
        return CommandResult.success();
      } else {
        TownMessage.warn(player, "You can't invite yourself!");
      }
    }

    return CommandResult.empty();
  }

  @Override
  public CommandElement[] getArguments() {
    return new CommandElement[]{
        GenericArguments.onlyOne(GenericArguments.player(Text.of("player")))
    };
  }
}
