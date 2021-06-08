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

    public void setPlotRentInfo(TownPlot plot, BigDecimal price, Duration period, TownsPermissionContext context) {
        RentInfo rentInfo = new RentInfo();
        rentInfo.setPeriod(period);
        rentInfo.setPrice(price);
        rentInfo.setPermissionContext(context);

        plot.setRentInfo(rentInfo);

        rentInfoRepository.saveOne(rentInfo);
        townPlotRepository.saveOne(plot);
    }

    public void setPlotRenter(TownPlot plot, Resident resident, int periods) {
        RentInfo rentInfo = plot.getRentInfo().get();

        rentInfo.setRenter(resident);
        rentInfo.setTimeRented(LocalDateTime.now());
        rentInfo.setPeriodsRented(periods);

        rentInfoRepository.saveOne(rentInfo);
    }
}
