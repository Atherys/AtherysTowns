package com.atherys.towns.commands.town;

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
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.text.Text;

import javax.annotation.Nullable;
import java.math.BigDecimal;

@Aliases("withdraw")
@Description("Used to withdraw currency from the town bank.")
@Permission("atherystowns.town.withdraw")
public class TownWithdrawCommand extends TownsCommand implements ParameterizedCommand {

    @Override
    protected CommandResult execute(Player player, CommandContext args, Resident resident,
                                    @Nullable Town town, @Nullable Nation nation) {
        if (town == null) {
            return CommandResult.empty();
        }

        if (AtherysTowns.getInstance().getEconomyService().isPresent()) {
            BigDecimal amount = BigDecimal.valueOf(args.<Double>getOne("amount").orElse(0.0d));
            Currency currency = args.<Currency>getOne("currency")
                    .orElse(AtherysTowns.getInstance().getEconomyService().get().getDefaultCurrency());

            if (town.getBank().isPresent()) {
                boolean result = town.withdraw(resident, amount, currency);
                if (result) {
                    town.informResidents(
                            Text.of(player.getName(), " has withdrawn ", amount.toString(), " ",
                                    currency.getDisplayName(), " from the town bank."));
                } else {
                    TownMessage.warn(player, "Withdraw Failed.");
                }
                return CommandResult.success();
            }
        }

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
