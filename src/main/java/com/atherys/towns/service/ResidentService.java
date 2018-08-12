package com.atherys.towns.service;

import com.atherys.towns.db.ResidentManager;
import com.google.inject.Inject;

public class ResidentService {

    private ResidentManager residentManager;

    @Inject
    public ResidentService(ResidentManager residentManager) {
        this.residentManager = residentManager;
    }
}
