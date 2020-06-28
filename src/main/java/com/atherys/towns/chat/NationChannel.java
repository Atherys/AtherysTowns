package com.atherys.towns.chat;

import com.atherys.chat.model.AtherysChannel;
import com.atherys.towns.AtherysTowns;
import com.atherys.towns.api.command.TownsCommandException;
import com.atherys.towns.model.Nation;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.channel.MessageReceiver;
import java.util.*;

public class NationChannel extends AtherysChannel {
    public static final String PERMISSION = "atherysparties.chat";

    public NationChannel() {
        super("nation");
        Set<String> aliases= new HashSet<>();
        aliases.add("nc");
        this.setAliases(aliases);
        this.setPermission(PERMISSION);
        this.setPrefix("&6[&eNation&6]&r");
        this.setSuffix("");
        this.setFormat("%cprefix %player: %message %csuffix");
        this.setName("&bNation");
    }


    @Override
    public Collection<MessageReceiver> getMembers(Object sender) {
        if(sender instanceof Player) {
            Nation playerNation;
            try {
                playerNation = AtherysTowns.getInstance().getNationFacade().getPlayerNation((Player) sender);
            } catch (TownsCommandException e) {
                return Collections.emptySet();
            }
            return new HashSet<>(AtherysTowns.getInstance().getNationFacade().getOnlineNationMembers(playerNation));
        }
        return Collections.emptySet();
    }
}
