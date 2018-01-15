package com.atherys.towns.town;

import com.atherys.towns.managers.TownManager;
import com.atherys.towns.nation.Nation;
import com.atherys.towns.plot.PlotFlags;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.UUID;

public class TownBuilder {

    Town town;

    TownBuilder ( Town town ) {
        this.town = town;
    }

    TownBuilder ( UUID uuid ) {
        town = new Town(uuid);
    }

    TownBuilder () {
        town = new Town( UUID.randomUUID() );
    }

    public TownBuilder nation ( Nation nation ) {
        if ( nation == null ) return this;
        town.setParent(nation);
        return this;
    }

    public TownBuilder status ( TownStatus status ) {
        town.setStatus( status );
        return this;
    }

    public TownBuilder flags ( PlotFlags flags ) {
        town.setFlags ( flags );
        return this;
    }

    public TownBuilder maxSize ( int max ) {
        town.setMaxSize(max);
        return this;
    }

    public TownBuilder spawn ( Location<World> spawn ) {
        town.setSpawn(spawn);
        return this;
    }

    public TownBuilder name ( String name ) {
        town.setName(name);
        return this;
    }

    public TownBuilder motd ( String motd ) {
        town.setMOTD( motd );
        return this;
    }

    public TownBuilder description ( String desc ) {
        town.setDescription(desc);
        return this;
    }

    public TownBuilder color ( TextColor color ) {
        town.setColor(color);
        return this;
    }

    public Town build() {
        TownManager.getInstance().add(town);
        return town;
    }

}
