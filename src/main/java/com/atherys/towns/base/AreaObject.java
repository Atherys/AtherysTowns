package com.atherys.towns.base;

import com.atherys.towns.AtherysTowns;
import com.atherys.towns.Settings;
import com.atherys.towns.resident.Resident;
import io.github.flibio.economylite.EconomyLite;
import math.geom2d.Point2D;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.math.BigDecimal;
import java.util.*;

public abstract class AreaObject<T extends BaseAreaObject> implements BaseAreaObject {

    private UUID uuid;

    protected List<T> contents;
    private UniqueAccount bank;

    protected AreaObject ( UUID uuid ) {
        this.uuid = uuid;
        contents = new LinkedList<>();
        createBank().ifPresent(uniqueAccount -> bank = uniqueAccount);
    }

    public UUID getUUID() { return uuid; }

    public boolean contains ( Location<World> loc ) {
        for ( T t : contents ) if ( t.contains(loc) ) return true;
        return false;
    }

    public boolean contains ( World w, double x, double y ) {
        for ( T t : contents ) if ( t.contains(w, x, y) ) return true;
        return false;
    }

    public boolean contains (World w, Point2D point ) {
        for ( T t : contents ) if ( t.contains(w, point) ) return true;
        return false;
    }

    public Optional<UniqueAccount> getBank() { return Optional.ofNullable(bank); }

    public boolean deposit ( Resident res, BigDecimal amount, Currency currency ) {
        if ( bank != null ) {
            Optional<UniqueAccount> acc = res.getBank();
            if ( acc.isPresent() ) {
                Cause cause = Cause.builder().named(NamedCause.of("towns-deposit", this)).build();
                UniqueAccount resAcc = acc.get();

                // if offered amount is less than 0
                if ( amount.compareTo(BigDecimal.ZERO) == -1 ) {
                    return false;
                }

                // if offered amount is more than what resident has
                if ( amount.compareTo(resAcc.getBalance(currency)) == 1 ) {
                    return false;
                }

                resAcc.withdraw(currency, amount, cause);
                bank.deposit(currency, amount, cause);
                return true;
            }
        }
        return false;
    }

    public boolean withdraw ( Resident res, BigDecimal amount, Currency currency ) {
        if ( bank != null ) {
            Optional<UniqueAccount> acc = res.getBank();
            if ( acc.isPresent() ) {
                Cause cause = Cause.builder().named(NamedCause.of("towns-withdraw", this)).build();
                UniqueAccount resAcc = acc.get();

                // if demanded amount is less than 0
                if ( amount.compareTo(BigDecimal.ZERO) == -1 ) {
                    return false;
                }

                // if demanded amount is more than what bank has
                if ( amount.compareTo(bank.getBalance(currency)) == 1 ) {
                    return false;
                }

                resAcc.deposit(currency, amount, cause);
                bank.withdraw(currency, amount, cause);
                return true;
            }
        }
        return false;
    }

    private Optional<UniqueAccount> createBank () {
        if ( AtherysTowns.getInstance().getEconomyPlugin().isPresent() ) {
            return EconomyLite.getEconomyService().getOrCreateAccount(uuid);
        } else return Optional.empty();
    }

    protected Text getFormattedBank() {
        if ( bank != null ) {
            Text.Builder builder = Text.builder();
            builder.append(Text.of(TextStyles.ITALIC, TextColors.DARK_GRAY, "( Hover to view )", TextStyles.RESET));
            Text.Builder hoverText = Text.builder();
            Iterator<Map.Entry<org.spongepowered.api.service.economy.Currency, BigDecimal>> iter = bank.getBalances().entrySet().iterator();
            while ( iter.hasNext() ) {
                Map.Entry<org.spongepowered.api.service.economy.Currency,BigDecimal> entry = iter.next();
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
