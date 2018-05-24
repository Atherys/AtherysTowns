package com.atherys.towns.api.plot;

import com.atherys.core.database.api.DBObject;
import com.atherys.core.views.Viewable;
import com.atherys.towns.api.AreaObject;
import com.atherys.towns.api.MetaHolder;
import com.atherys.towns.api.plot.flag.FlagHolder;
import com.atherys.towns.api.town.ITown;
import com.atherys.towns.views.PlotView;
import org.spongepowered.api.util.annotation.NonnullByDefault;

/**
 * A plot is the simplest AreaObject. It is an arbitrary rectangle which contains flags and meta.
 */
public interface IPlot extends DBObject, AreaObject, MetaHolder, FlagHolder, Viewable<PlotView> {

    /**
     * All plots must have a Town parent. This method retrieves it.
     * @return The town this plot belongs to
     */
    @NonnullByDefault
    ITown getTown();

}
