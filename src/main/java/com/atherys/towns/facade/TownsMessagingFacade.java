package com.atherys.towns.facade;

import com.atherys.core.economy.Economy;
import com.atherys.core.utils.AbstractMessagingFacade;
import com.atherys.towns.AtherysTowns;
import com.atherys.towns.TownsConfig;
import com.atherys.towns.model.entity.Nation;
import com.atherys.towns.model.entity.Town;
import com.atherys.towns.service.NationService;
import com.atherys.towns.service.TownService;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;

import java.util.UUID;

import static org.spongepowered.api.text.format.TextColors.*;

@Singleton
public class TownsMessagingFacade extends AbstractMessagingFacade {

    @Inject
    private TownsConfig config;

    @Inject
    private NationFacade nationFacade;

    @Inject
    private TownFacade townFacade;

    public TownsMessagingFacade() {
        super("Towns");
    }

    public void broadcastInfo(Object... message) {
        Sponge.getServer().getBroadcastChannel().send(formatInfo(message));
    }

    public void broadcastError(Object... message) {
        Sponge.getServer().getBroadcastChannel().send(formatError(message));
    }

    public void broadcastNationInfo(Nation nation, Object... message) {
        Text formattedMessage = formatInfo(message);
        nationFacade.getOnlineNationMembers(nation).stream().forEach(
                player -> player.sendMessage(formattedMessage)
        );
    }

    public void broadcastTownInfo(Town town, Object... message) {
        Text formattedMessage = formatInfo(message);
        townFacade.getOnlineTownMembers(town).stream().forEach(
                player -> player.sendMessage(formattedMessage)
        );
    }

    public Text renderBoolean(boolean value, boolean upperCase) {
        if (value) {
            return Text.of(GREEN, upperCase ? "True" : "true");
        }

        return Text.of(RED, upperCase ? "False" : "false");
    }

    public int getPadding(int length) {
        if (length < 10) {
            return 18;
        } else if (length < 20) {
            return 14;
        } else if (length < 30) {
            return 8;
        } else if (length < 40) {
            return 4;
        } else {
            return 2;
        }
    }

    public Text createTownsHeader(String subjectName) {
        int padding = getPadding(subjectName.length());
        String trimmedName = subjectName;

        if (padding < 4) {
            trimmedName = subjectName.substring(0, 41) + "...";
        }

        return Text.builder()
                .append(Text.of(DARK_GRAY, "[]", StringUtils.repeat("=", padding), "[ "))
                .append(Text.of(TextActions.showText(Text.of(GOLD, subjectName)), GOLD, trimmedName))
                .append(Text.of(DARK_GRAY, " ]", StringUtils.repeat("=", padding), "[]", Text.NEW_LINE))
                .build();
    }

    public Text renderBank(String identifier) {
        if (AtherysTowns.economyIsEnabled()) {
            return Economy.getAccount(identifier)
                    .map(this::renderBank)
                    .orElse(Text.EMPTY);
        }

        return Text.EMPTY;
    }

    public Text renderBank(UUID id) {
        if (AtherysTowns.economyIsEnabled()) {
            return Economy.getAccount(id)
                    .map(this::renderBank)
                    .orElse(Text.EMPTY);
        }

        return Text.EMPTY;
    }

    public Text renderBank(Account account) {
        if (AtherysTowns.economyIsEnabled()) {
            return Text.of(DARK_GREEN, "Bank: ", GOLD, config.DEFAULT_CURRENCY.format(account.getBalance(config.DEFAULT_CURRENCY)));
        }
        return Text.EMPTY;
    }

}
