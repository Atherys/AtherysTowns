package com.atherys.towns.listener;

import com.atherys.towns.facade.TownRaidFacade;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.DestructEntityEvent;

@Singleton
public class RaidListener {

    @Inject
    TownRaidFacade townRaidFacade;

    @Listener
    public void onRaidPointDeath(DestructEntityEvent.Death event) {
        if (townRaidFacade.isEntityRaidPoint(event.getTargetEntity())) {
            townRaidFacade.onRaidPointDeath(event);
        }
    }
}
