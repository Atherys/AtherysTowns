package com.atherys.towns.service;

import com.atherys.towns.db.TownManager;
import com.google.inject.Inject;

public class TownService {

    private TownManager townManager;

    @Inject
    public TownService(TownManager townManager) {
        this.townManager = townManager;
    }
}
