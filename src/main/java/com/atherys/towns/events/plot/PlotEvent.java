package com.atherys.towns.events.plot;

import com.atherys.towns.api.plot.IPlot;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.cause.Cause;

public abstract class PlotEvent implements Event {

    private Cause cause;
    private IPlot plot;

    protected PlotEvent(IPlot plot) {
        this.plot = plot;
        this.cause = Cause.builder()
            .append(plot)
            .build(Sponge.getCauseStackManager().getCurrentContext());
    }

    public IPlot getPlot() {
        return plot;
    }

    @Override
    public Cause getCause() {
        return cause;
    }
}
