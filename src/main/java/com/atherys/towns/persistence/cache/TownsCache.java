package com.atherys.towns.persistence.cache;

import com.atherys.core.db.cache.Cache;
import com.atherys.core.db.cache.SimpleCache;
import com.atherys.towns.model.entity.*;
import com.atherys.towns.persistence.*;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

import java.util.UUID;

@Singleton
public class TownsCache {

    @Inject
    private Provider<ResidentRepository> residentRepository;

    @Inject
    private Provider<TownPlotRepository> townPlotRepository;

    @Inject
    private Provider<RentInfoRepository> rentInfoRepository;

    @Inject
    private Provider<TownRepository> townRepository;

    @Inject
    private Provider<NationRepository> nationRepository;

    @Inject
    private Provider<NationPlotRepository> nationPlotRepository;

    private Cache<Resident, UUID> residentCache = new SimpleCache<>();

    private TownPlotCache townPlotCache = new TownPlotCache();

    private Cache<RentInfo, Long> rentInfoCache = new SimpleCache<>();

    private Cache<NationPlot, Long> nationPlotCache = new SimpleCache<>();

    private Cache<Town, Long> townCache = new SimpleCache<>();

    private Cache<Nation, Long> nationCache = new SimpleCache<>();

    public TownsCache() {
    }

    public void initCache() {
        residentRepository.get().initCache();
        townRepository.get().initCache();
        townPlotRepository.get().initCache();
        rentInfoRepository.get().initCache();
        nationRepository.get().initCache();
        nationPlotRepository.get().initCache();
    }

    public void flushCache() {
        residentRepository.get().flushCache();
        townPlotRepository.get().flushCache();
        townRepository.get().flushCache();
        rentInfoRepository.get().flushCache();
        nationRepository.get().flushCache();
        nationPlotRepository.get().flushCache();
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

    public Cache<RentInfo, Long> getRentInfoCache() {
        return rentInfoCache;
    }
}
