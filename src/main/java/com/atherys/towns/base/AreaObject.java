package com.atherys.towns.base;

import com.atherys.towns.AtherysTowns;
import com.atherys.towns.Settings;
import com.atherys.towns.resident.Resident;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public abstract class AreaObject<T extends BaseAreaObject> implements BaseAreaObject {

    protected UUID uuid;
    private T parent;
    private UniqueAccount bank;

    protected AreaObject ( UUID uuid ) {
        this.uuid = uuid;
        createBank().ifPresent(uniqueAccount -> bank = uniqueAccount);
    }

    public UUID getUUID() { return uuid; }

    public Optional<UniqueAccount> getBank() { return Optional.ofNullable(bank); }

    public boolean deposit ( Resident res, BigDecimal amount, Currency currency ) {
        if ( bank != null) {
            Optional<UniqueAccount> acc = res.getBank();
            if ( acc.isPresent() ) {
                UniqueAccount resAcc = acc.get();
                Cause cause = Cause.builder().named( NamedCause.of ( "towns-deposit", this) ).build();

                resAcc.transfer( bank, currency, amount, cause );
                return true;
            }
        }
        return false;
    }

    public boolean withdraw ( Resident res, BigDecimal amount, Currency currency ) {
        if ( bank != null ) {
            Optional<UniqueAccount> acc = res.getBank();
            if ( acc.isPresent() ) {
                Cause cause = Cause.builder().named( NamedCause.of ( "towns-withdraw", this) ).build();
                UniqueAccount resAcc = acc.get();

                bank.transfer( resAcc, currency, amount, cause );
                return true;
            }
        }
        return false;
    }

    public void setParent ( T parent ) { this.parent = parent; }

    public Optional<T> getParent() { return Optional.ofNullable(parent); }

    private Optional<UniqueAccount> createBank () {
        Optional<EconomyService> economy = AtherysTowns.getInstance().getEconomyService();
        return economy.flatMap(economyService -> economyService.getOrCreateAccount(uuid));
    }

    protected Text getFormattedBank() {
        if ( bank != null ) {
            Text.Builder builder = Text.builder();
            builder.append(Text.of(TextStyles.ITALIC, TextColors.DARK_GRAY, "( Hover to view )", TextStyles.RESET));
            Text.Builder hoverText = Text.builder();
            Iterator<Map.Entry<Currency, BigDecimal>> iter = bank.getBalances().entrySet().iterator();
            while ( iter.hasNext() ) {
                Map.Entry<Currency,BigDecimal> entry = iter.next();
                hoverText.append(Text.of(Settings.SECONDARY_COLOR, entry.getValue(), Settings.DECORATION_COLOR, " ", entry.getKey().getDisplayName() ) );
                if ( iter.hasNext() ) {
                    hoverText.append(Text.of("\n"));
                }
            }
            return builder.onHover(TextActions.showText(hoverText.build())).build();
        } else {
            return Text.of("None");
        }
    }
}
