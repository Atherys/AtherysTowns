package com.atherys.towns.chat;

import com.atherys.chat.model.AtherysChannel;
import com.atherys.towns.AtherysTowns;
import com.atherys.towns.model.entity.Nation;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.channel.MessageReceiver;

import java.util.*;

public class NationChannel extends AtherysChannel {
    public static final String PERMISSION = "atherystowns.nation.chat";

    public NationChannel() {
        super("nation");
        Set<String> aliases = new HashSet<>();
        aliases.add("nc");
        this.setAliases(aliases);
        this.setPermission(PERMISSION);
        this.setPrefix(AtherysTowns.getInstance().getConfig().NATION_CHAT_PREFIX);
        this.setSuffix("");
        this.setFormat("%cprefix %player: %message %csuffix");
        this.setName("&bNation");
    }

    @Override
    public Collection<MessageReceiver> getMembers(Object sender) {

        if (sender instanceof Player) {
            Optional<Nation> playerNation = AtherysTowns.getInstance().getResidentFacade().getPlayerNation((Player) sender);
            if (playerNation.isPresent()) {
                return new HashSet<>(AtherysTowns.getInstance().getNationFacade().getOnlineNationMembers(playerNation.get()));
            }
        }

        return Collections.emptySet();
    }
}
