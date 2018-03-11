package com.atherys.towns.commands.town;

import com.atherys.towns.AtherysTowns;
import com.atherys.towns.commands.TownsSimpleCommand;
import com.atherys.towns.messaging.TownMessage;
import com.atherys.towns.nation.Nation;
import com.atherys.towns.permissions.actions.TownActions;
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

public class TownDepositCommand extends TownsSimpleCommand {

    private static TownDepositCommand instance = new TownDepositCommand();

    public static TownDepositCommand getInstance () {
        return instance;
    }

    @Override
    protected CommandResult execute ( Player player, CommandContext args, Resident resident, @Nullable Town town, @Nullable Nation nation ) {
        if ( town == null ) return CommandResult.empty();

        if ( AtherysTowns.getInstance().getEconomyService().isPresent() ) {
            BigDecimal amount = BigDecimal.valueOf( args.<Double>getOne( "amount" ).orElse( 0.0d ) );
            Currency currency = args.<Currency>getOne( "currency" ).orElse( AtherysTowns.getInstance().getEconomyService().get().getDefaultCurrency() );

            if ( town.getBank().isPresent() ) {
                boolean result = town.deposit( resident, amount, currency );
                if ( result )
                    town.informResidents( Text.of( player.getName(), " has deposited ", amount.toString(), " ", currency.getPluralDisplayName(), " into the town bank." ) );
                else TownMessage.warn( player, "Deposit Failed." );
                return CommandResult.success();
            }
        }

        return CommandResult.empty();
    }

    @Override
    public CommandSpec getSpec () {
        return CommandSpec.builder()
                .description( Text.of( "Used to deposit money into the town bank." ) )
                .permission( TownActions.TOWN_DEPOSIT.getPermission() )
                .arguments(
                        GenericArguments.doubleNum( Text.of( "amount" ) ),
                        GenericArguments.optional( GenericArguments.catalogedElement( Text.of( "currency" ), Currency.class ) )
                )
                .executor( this )
                .build();
    }
}
