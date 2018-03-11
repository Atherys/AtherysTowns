package com.atherys.towns.nation;

import com.atherys.towns.managers.NationManager;
import org.spongepowered.api.text.format.TextColor;

import java.util.UUID;

public class NationBuilder {

    private Nation nation;

    NationBuilder ( UUID uuid ) {
        nation = new Nation( uuid );
    }

    public NationBuilder name ( String name ) {
        nation.setName( name );
        return this;
    }

    public NationBuilder color ( TextColor color ) {
        nation.setColor( color );
        return this;
    }

    public NationBuilder description ( String description ) {
        nation.setDescription( description );
        return this;
    }

    public NationBuilder leaderTitle ( String title ) {
        nation.setLeaderTitle( title );
        return this;
    }

    public NationBuilder tax ( double tax ) {
        nation.setTax( tax );
        return this;
    }

    public Nation build () {
        NationManager.getInstance().add( nation );
        return nation;
    }
}
