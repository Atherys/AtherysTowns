package com.atherys.towns.service;

import com.atherys.towns.persistence.PlotRepository;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class PlotService {

    @Inject
    PlotRepository plotRepository;

    PlotService() {
    }
}
