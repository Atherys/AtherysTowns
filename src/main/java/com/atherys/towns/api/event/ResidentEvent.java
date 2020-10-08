package com.atherys.towns.api.event;

import com.atherys.towns.AtherysTowns;
import com.atherys.towns.model.entity.Resident;
import com.atherys.towns.model.entity.Town;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.cause.Cause;

public abstract class ResidentEvent implements Event {
    private final Cause cause;

    private final Resident resident;

    protected ResidentEvent(Resident resident) {
        this.cause = Cause.builder().append(resident).append(AtherysTowns.getInstance()).build(Sponge.getCauseStackManager().getCurrentContext());
        this.resident = resident;
    }

    public Resident getResident() {
        return resident;
    }

    @Override
    public Cause getCause() {
        return cause;
    }

    public static class JoinedTown extends ResidentEvent {
        private final Town town;

        public JoinedTown(Resident resident, Town town) {
            super(resident);
            this.town = town;
        }

        public Town getTown() {
            return town;
        }
    }

    public static class LeftTown extends ResidentEvent {
        private final Town town;

        public LeftTown(Resident resident, Town town) {
            super(resident);
            this.town = town;
        }

        public Town getTown() {
            return town;
        }
    }
}
