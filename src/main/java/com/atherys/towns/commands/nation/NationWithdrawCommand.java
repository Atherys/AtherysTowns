package com.atherys.towns.commands.nation;

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
import java.math.BigDecimal;
import javax.annotation.Nullable;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.text.Text;

@Aliases("withdraw")
@Description("Used to withdraw money from the nations bank")
@Permission("atherystowns.nation.withdraw")
public class NationWithdrawCommand extends TownsCommand implements ParameterizedCommand {

  @Override
  protected CommandResult execute(Player player, CommandContext args, Resident resident,
      @Nullable Town town, @Nullable Nation nation) {
    if (!args.getOne("amount").isPresent()) {
      TownMessage.warn(player, "You must provide an amount to withdraw.");
      return CommandResult.empty();
    }

    if (AtherysTowns.getInstance().getEconomyService().isPresent() && nation != null) {
      BigDecimal amount = BigDecimal.valueOf(args.<Double>getOne("amount").orElse(0.0d));
      Currency currency = args.<Currency>getOne("currency")
          .orElse(AtherysTowns.getInstance().getEconomyService().get().getDefaultCurrency());

      if (nation.withdraw(resident, amount, currency)) {
        nation.informResidents(
            Text.of(player.getName(), " has withdrawn ", amount.toString(), " ",
                currency.getPluralDisplayName(), " into the nation bank."));
      } else {
        TownMessage.warn(player, "Withdraw Failed.");
      }
      return CommandResult.success();
    }

    TownMessage.warn(player, "Nation withdraw failed.");
    return CommandResult.empty();
  }

  @Override
  public CommandElement[] getArguments() {
    return new CommandElement[]{
        GenericArguments.doubleNum(Text.of("amount")),
        GenericArguments.optional(
            GenericArguments.catalogedElement(Text.of("currency"), Currency.class))
    };
  }
}
