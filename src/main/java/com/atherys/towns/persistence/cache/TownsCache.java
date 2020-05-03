package com.atherys.towns.persistence.cache;

import com.atherys.core.db.cache.Cache;
import com.atherys.core.db.cache.SimpleCache;
import com.atherys.towns.config.NationConfig;
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
    private PlotRepository plotRepository;

    @Inject
    private TownRepository townRepository;

    @Inject
    private NationRepository nationRepository;

    @Inject
    private PermissionRepository permissionRepository;

    private Cache<Resident, UUID> residentCache = new SimpleCache<>();

    private PlotCache plotCache = new PlotCache();

    private Cache<Town, Long> townCache = new SimpleCache<>();

    private Cache<NationConfig, Long> nationCache = new SimpleCache<>();

    public TownsCache() {
    }

    public void initCache() {
        residentRepository.initCache();

        townRepository.initCache();
        plotRepository.initCache();
        nationRepository.initCache();

        permissionRepository.initCache();
    }

    public void flushCache() {
        residentRepository.flushCache();
        plotRepository.flushCache();
        townRepository.flushCache();
        nationRepository.flushCache();
        permissionRepository.flushCache();
    }

    public Cache<NationConfig, Long> getNationCache() {
        return nationCache;
    }

    public Cache<Resident, UUID> getResidentCache() {
        return residentCache;
    }

    public PlotCache getPlotCache() {
        return plotCache;
    }

    public Cache<Town, Long> getTownCache() {
        return townCache;
    }
}
