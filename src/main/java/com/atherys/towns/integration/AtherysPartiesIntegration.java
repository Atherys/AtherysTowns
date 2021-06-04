package com.atherys.towns.integration;

import com.atherys.party.AtherysParties;
import com.atherys.party.entity.Party;
import org.spongepowered.api.entity.living.player.Player;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public final class AtherysPartiesIntegration {
    public static boolean playerHasParty(Player player) {
        return AtherysParties.getInstance().getPartyFacade().getPlayerParty(player).isPresent();
    }

    public static Set<Player> fetchPlayerPartyMembers(Player player) {
        Optional<Party> playerParty = AtherysParties.getInstance().getPartyFacade().getPlayerParty(player);

        if (playerParty.isPresent()) {
            return AtherysParties.getInstance().getPartyFacade().getOnlinePartyMembers(playerParty.get());
        } else {
            return new HashSet<>();
        }
    }
}
