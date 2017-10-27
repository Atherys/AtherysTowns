package com.atherys.towns.commands.nation;

import com.atherys.towns.AtherysTowns;
import com.atherys.towns.messaging.TownMessage;
import com.atherys.towns.nation.Nation;
import com.atherys.towns.resident.Resident;
import com.atherys.towns.resident.ranks.NationRank;
import com.atherys.towns.town.Town;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.text.Text;

import javax.annotation.Nullable;
import java.math.BigDecimal;

public class NationWithdrawCommand extends AbstractNationCommand {

    protected NationWithdrawCommand() {
        super(
                new String[] { "withdraw" },
                "withdraw <amount> [currency]",
                Text.of("Used to withdraw money from the nation bank."),
                NationRank.Action.NATION_WITHDRAW,
                true,
                true,
                true,
                true
        );
    }

    @Override
    public CommandResult townsExecute(@Nullable Nation nation, @Nullable Town town, Resident resident, Player player, CommandContext args) {
        if ( !args.getOne("amount").isPresent() ) {
            TownMessage.warn(player, "You must provide an amount to withdraw.");
            return CommandResult.empty();
        }

        if ( AtherysTowns.getInstance().getEconomyService().isPresent() && nation != null ) {
            BigDecimal amount = BigDecimal.valueOf( args.<Double>getOne("amount").orElse(0.0d) );
            Currency currency = args.<Currency>getOne("currency").orElse(AtherysTowns.getInstance().getEconomyService().get().getDefaultCurrency());

            if ( nation.withdraw(resident, amount, currency) )
                nation.informResidents ( Text.of ( player.getName(), " has withdrawn ", amount.toString(), " ", currency.getPluralDisplayName(), " into the nation bank." ) );
            else TownMessage.warn(player, "Withdraw Failed.");
            return CommandResult.success();
        }

        TownMessage.warn(player, "Nation withdraw failed.");
        return CommandResult.empty();
    }

    @Override
    public CommandSpec getSpec() {
        return CommandSpec.builder()
                .permission("atherys.town.commands.town.withdraw")
                .description( Text.of("Used to withdraw money from the town's bank") )
                .arguments(
                        GenericArguments.doubleNum(Text.of("amount")),
                        GenericArguments.optional( GenericArguments.catalogedElement(Text.of("currency"), Currency.class) )
                )
                .executor(this)
                .build();
    }
}
