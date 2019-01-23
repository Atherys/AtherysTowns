package com.atherys.towns.chat;

import com.atherys.towns.AtherysTowns;
import com.atherys.towns.entity.Resident;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.channel.AbstractMutableMessageChannel;

import java.util.Collection;
import java.util.Optional;

public class ResidentMessageChannel extends UUIDMessageChannel {

    public ResidentMessageChannel() {
    }

    public ResidentMessageChannel(Collection<Resident> residents) {
        residents.forEach(this::addResident);
    }

    public boolean addResident(Resident resident) {
        return addMember(resident.getId());
    }

    public boolean removeResident(Resident resident) {
        return removeMember(resident.getId());
    }

}
