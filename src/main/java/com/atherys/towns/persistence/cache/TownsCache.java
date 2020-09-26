package com.atherys.towns.persistence.cache;

import com.atherys.core.db.cache.Cache;
import com.atherys.core.db.cache.SimpleCache;
import com.atherys.towns.model.entity.Nation;
import com.atherys.towns.model.entity.NationPlot;
import com.atherys.towns.model.entity.Resident;
import com.atherys.towns.model.entity.Town;
import com.atherys.towns.persistence.*;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.UUID;

@Singleton
public class TownsCache {

    @Inject
    private ResidentRepository residentRepository;

    @Inject
    private TownPlotRepository townPlotRepository;

    @Inject
    private TownRepository townRepository;

    @Inject
    private NationRepository nationRepository;

    @Inject
    private NationPlotRepository nationPlotRepository;

    private Cache<Resident, UUID> residentCache = new SimpleCache<>();

    private TownPlotCache townPlotCache = new TownPlotCache();

    private Cache<NationPlot, Long> nationPlotCache = new SimpleCache<>();

    private Cache<Town, Long> townCache = new SimpleCache<>();

    private Cache<Nation, Long> nationCache = new SimpleCache<>();

    public TownsCache() {
    }

    public void initCache() {
        residentRepository.initCache();
        townRepository.initCache();
        townPlotRepository.initCache();
        nationRepository.initCache();
        nationPlotRepository.initCache();
    }

    public void flushCache() {
        residentRepository.flushCache();
        townPlotRepository.flushCache();
        townRepository.flushCache();
        nationRepository.flushCache();
        nationPlotRepository.flushCache();
    }

    public Cache<Resident, UUID> getResidentCache() {
        return residentCache;
    }

    public TownPlotCache getTownPlotCache() {
        return townPlotCache;
    }

    public Cache<Town, Long> getTownCache() {
        return townCache;
    }

    public Cache<Nation, Long> getNationCache() {
        return nationCache;
    }

    public Cache<NationPlot, Long> getNationPlotCache() {
        return nationPlotCache;
    }
}
