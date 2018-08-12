package com.atherys.towns.service;

import com.atherys.towns.db.NationManager;
import com.google.inject.Inject;

public class NationService {

    private NationManager nationManager;

    @Inject
    public NationService(NationManager nationManager) {
        this.nationManager = nationManager;
    }
}
