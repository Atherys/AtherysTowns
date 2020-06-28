package com.atherys.towns.chat;

import com.atherys.chat.model.AtherysChannel;
import com.atherys.towns.AtherysTowns;
import com.atherys.towns.model.entity.Town;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.channel.MessageReceiver;

import java.util.*;

public class TownChannel extends AtherysChannel {
    public static final String PERMISSION = "atherysparties.chat";

    public TownChannel() {
        super("town");
        Set<String> aliases = new HashSet<>();
        aliases.add("tc");
        this.setAliases(aliases);
        this.setPermission(PERMISSION);
        this.setPrefix("&2[&bTown&2]&r");
        this.setSuffix("");
        this.setFormat("%cprefix %player: %message %csuffix");
        this.setName("&bTown");
    }


    @Override
    public Collection<MessageReceiver> getMembers(Object sender) {
        if (sender instanceof Player) {
            Optional<Town> playerTown = AtherysTowns.getInstance().getResidentFacade().getPlayerTown((Player) sender);
            if (playerTown.isPresent()) {
                return new HashSet<>(AtherysTowns.getInstance().getTownFacade().getOnlineTownMembers(playerTown.get()));
            }
        }

        return Collections.emptySet();
    }
}
