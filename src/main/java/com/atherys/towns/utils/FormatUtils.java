package com.atherys.towns.utils;

import com.atherys.towns.AtherysTowns;
import com.atherys.towns.base.AbstractAreaObject;
import com.atherys.towns.resident.Resident;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import javax.annotation.Nonnull;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

public final class FormatUtils {

    public static Text getFormattedBank(AbstractAreaObject<?> ao) {
        Optional<UniqueAccount> bank = ao.getBank();
        return bank.map(FormatUtils::getFormattedBank).orElseGet(() -> Text.of("None"));
    }

    public static Text getFormattedBank(Resident resident) {
        Optional<UniqueAccount> bank = resident.getBank();
        return bank.map(FormatUtils::getFormattedBank).orElseGet(() -> Text.of("None"));
    }

    public static Text getFormattedBank(@Nonnull UniqueAccount bank) {
        Text.Builder builder = Text.builder();
        builder.append(Text.of(TextStyles.ITALIC, TextColors.DARK_GRAY, "( Hover to view )",
                TextStyles.RESET));
        Text.Builder hoverText = Text.builder();
        Iterator<Map.Entry<Currency, BigDecimal>> iter = bank.getBalances().entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<Currency, BigDecimal> entry = iter.next();
            hoverText.append(Text.of(AtherysTowns.getConfig().COLORS.PRIMARY, entry.getValue(),
                    AtherysTowns.getConfig().COLORS.DECORATION, " ", entry.getKey().getDisplayName()));
            if (iter.hasNext()) {
                hoverText.append(Text.of("\n"));
            }
        }
        return builder.onHover(TextActions.showText(hoverText.build())).build();
    }

}
