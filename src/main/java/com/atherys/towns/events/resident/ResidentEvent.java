package com.atherys.towns.events.resident;

import com.atherys.towns.api.resident.IResident;
import org.spongepowered.api.event.Event;

public abstract class ResidentEvent implements Event {

    private IResident resident;

    protected ResidentEvent(IResident resident) {
        this.resident = resident;
    }

    public IResident getResident() {
        return resident;
    }
}
