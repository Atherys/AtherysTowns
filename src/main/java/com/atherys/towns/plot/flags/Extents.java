package com.atherys.towns.plot.flags;

import com.atherys.towns.managers.NationManager;
import com.atherys.towns.nation.Nation;
import com.atherys.towns.town.Town;

import java.util.Optional;

public final class Extents {

    public static final Extent NONE = new Extent( "none", "None", ( res, flag, plot ) -> false );

    public static final Extent ANY = new Extent( "any", "Any", ( res, flag, plot ) -> true );

    public static final Extent TOWN = new Extent( "town", "Town Only", ( res, flag, plot ) -> {
        Optional<Town> town = res.getTown();
        return town.isPresent() && town.get().equals( plot.getParent().get() );
    } );

    public static final Extent NATION = new Extent( "nation", "Nation Only", ( res, flag, plot ) -> {
        Optional<Nation> resNation = NationManager.getInstance().getByResident( res );
        Optional<Nation> plotNation = NationManager.getInstance().getByPlot( plot );
        // if resident has no nation, and the plot has a nation flag, then that means the resident's permission is indeterminate. Return false;
        // if plot has no nation, but has a nation flag, that means nobody who is part of a nation should have permission;
        if ( !resNation.isPresent() || !plotNation.isPresent() ) return false;
        // if resident's town and plot's town have same nation
        return res.getTown().isPresent() && resNation.get().equals( plotNation.get() );
    } );

}
