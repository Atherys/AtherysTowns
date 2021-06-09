package com.atherys.towns.service;

import com.atherys.towns.api.permission.TownsPermissionContext;
import com.atherys.towns.model.entity.RentInfo;
import com.atherys.towns.model.entity.Resident;
import com.atherys.towns.model.entity.TownPlot;
import com.atherys.towns.persistence.RentInfoRepository;
import com.atherys.towns.persistence.TownPlotRepository;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;

@Singleton
public class RentService {
    @Inject
    private TownPlotRepository townPlotRepository;

    @Inject
    private RentInfoRepository rentInfoRepository;

    public void init() {

    }

    public void setPlotRentInfo(TownPlot plot, BigDecimal price, Duration period, TownsPermissionContext context) {
        RentInfo rentInfo = new RentInfo();
        rentInfo.setPeriod(period);
        rentInfo.setPrice(price);
        rentInfo.setPermissionContext(context);

        plot.setRentInfo(rentInfo);

        rentInfoRepository.saveOne(rentInfo);
        townPlotRepository.saveOne(plot);
    }

    public void setPlotRenter(RentInfo rentInfo, Resident resident, int periods) {
        rentInfo.setRenter(resident);
        rentInfo.setTimeRented(LocalDateTime.now());
        rentInfo.setPeriodsRented(periods);

        rentInfoRepository.saveOne(rentInfo);
    }

    public void setRentPeriods(RentInfo rentInfo, int periods) {
        rentInfo.setPeriodsRented(periods);
        rentInfoRepository.saveOne(rentInfo);
    }

    public void clearPlotRenter(RentInfo rentInfo) {
        rentInfo.setRenter(null);
        rentInfo.setPeriodsRented(0);
        rentInfo.setTimeRented(null);

        rentInfoRepository.saveOne(rentInfo);
    }
}
