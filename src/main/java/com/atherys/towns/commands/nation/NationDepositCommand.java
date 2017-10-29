package com.atherys.towns.commands.nation;

import com.atherys.towns.AtherysTowns;
import com.atherys.towns.commands.TownsSimpleCommand;
import com.atherys.towns.messaging.TownMessage;
import com.atherys.towns.nation.Nation;
import com.atherys.towns.permissions.actions.NationAction;
import com.atherys.towns.resident.Resident;
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

public class NationDepositCommand extends TownsSimpleCommand {

    private static NationDepositCommand instance = new NationDepositCommand();

    public static NationDepositCommand getInstance() {
        return instance;
    }

    @Override
    protected CommandResult execute(Player player, CommandContext args, Resident resident, @Nullable Town town, @Nullable Nation nation) {
        if ( !args.getOne("amount").isPresent() ) {
            TownMessage.warn(player, "You must provide an amount to deposit.");
            return CommandResult.empty();
        }

        if ( AtherysTowns.getInstance().getEconomyService().isPresent() && nation != null ) {
            BigDecimal amount = BigDecimal.valueOf( args.<Double>getOne("amount").orElse(0.0d) );
            Currency currency = args.<Currency>getOne("currency").orElse(AtherysTowns.getInstance().getEconomyService().get().getDefaultCurrency());

            if ( nation.deposit(resident, amount, currency) )
                nation.informResidents ( Text.of ( player.getName(), " has deposited ", amount.toString(), " ", currency.getPluralDisplayName(), " into the nation bank." ) );
            else TownMessage.warn(player, "Deposit Failed.");
            return CommandResult.success();
        }

        TownMessage.warn(player, "Nation deposit failed.");
        return CommandResult.empty();
    }

    @Override
    public CommandSpec getSpec() {
        return CommandSpec.builder()
                .permission(NationAction.NATION_DEPOSIT.getPermission())
                .description( Text.of("Used to deposit money into the town's bank") )
                .arguments(
                        GenericArguments.doubleNum(Text.of("amount")),
                        GenericArguments.optional( GenericArguments.catalogedElement(Text.of("currency"), Currency.class) )
                )
                .executor(this)
                .build();
    }
}
