package com.atherys.towns.commands.town;

import com.atherys.towns.AtherysTowns;
import com.atherys.towns.commands.TownsSimpleCommand;
import com.atherys.towns.messaging.TownMessage;
import com.atherys.towns.nation.Nation;
import com.atherys.towns.permissions.actions.TownActions;
import com.atherys.towns.resident.Resident;
import com.atherys.towns.town.Town;
import java.math.BigDecimal;
import javax.annotation.Nullable;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.text.Text;

public class TownWithdrawCommand extends TownsSimpleCommand {

    private static TownWithdrawCommand instance = new TownWithdrawCommand();

    public static TownWithdrawCommand getInstance() {
        return instance;
    }

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
    public CommandSpec getSpec() {
        return CommandSpec.builder()
            .description(Text.of("Used to withdraw money from the town bank."))
            .permission(TownActions.TOWN_WITHDRAW.getPermission())
            .arguments(
                GenericArguments.doubleNum(Text.of("amount")),
                GenericArguments.optional(
                    GenericArguments.catalogedElement(Text.of("currency"), Currency.class))
            )
            .executor(new TownWithdrawCommand())
            .build();
    }
}
