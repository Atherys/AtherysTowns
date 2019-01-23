package com.atherys.towns.chat;

import com.atherys.towns.entity.Resident;

import java.util.Collection;

public class ResidentMessageChannel extends MutableUUIDMessageChannel {

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
