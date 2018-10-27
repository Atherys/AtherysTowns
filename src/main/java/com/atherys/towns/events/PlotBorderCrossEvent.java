package com.atherys.towns.events;

import com.atherys.towns.model.Plot;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;

public class PlotBorderCrossEvent implements Cancellable, Event {

    private final Plot from;
    private final Plot to;
    private boolean cancelled;

    public PlotBorderCrossEvent(Plot from, Plot to) {
        this.from = from;
        this.to = to;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    public Cause getCause() {
        return Cause.builder().build(EventContext.empty()); 
    }
}
