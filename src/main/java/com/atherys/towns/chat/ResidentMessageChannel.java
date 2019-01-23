package com.atherys.towns.chat;

import com.atherys.towns.AtherysTowns;
import com.atherys.towns.entity.Resident;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.channel.AbstractMutableMessageChannel;

import java.util.Collection;
import java.util.Optional;

public class ResidentMessageChannel extends AbstractMutableMessageChannel {

    public ResidentMessageChannel() {
    }

    public ResidentMessageChannel(Collection<Resident> residents) {
        residents.forEach(this::addResident);
    }

    public void addResident(Resident resident) {
        Optional<Player> player = AtherysTowns.getInstance().getResidentService().getPlayerFromResident(resident);
        player.ifPresent(this::addMember);
    }

    public void removeResident(Resident resident) {
        Optional<Player> player = AtherysTowns.getInstance().getResidentService().getPlayerFromResident(resident);
        player.ifPresent(this::removeMember);
    }

}
