package com.atherys.towns.service;

import com.atherys.towns.AtherysTowns;
import com.atherys.towns.TownsConfig;
import com.atherys.towns.api.permission.TownsPermissionContext;
import com.atherys.towns.model.entity.RentInfo;
import com.atherys.towns.model.entity.Resident;
import com.atherys.towns.model.entity.TownPlot;
import com.atherys.towns.persistence.RentInfoRepository;
import com.atherys.towns.persistence.ResidentRepository;
import com.atherys.towns.persistence.TownPlotRepository;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.spongepowered.api.scheduler.Task;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Singleton
public class RentService {
    @Inject
    private TownPlotRepository townPlotRepository;

    @Inject
    private RentInfoRepository rentInfoRepository;

    @Inject
    private ResidentRepository residentRepository;

    @Inject
    private TownsConfig townsConfig;

    public void init() {
        Task.builder()
                .interval(townsConfig.TOWN.RENT_CONFIG.RENT_CHECK_INTERVAL.toMinutes(), TimeUnit.MINUTES)
                .execute(() -> {
                    for (RentInfo rentInfo : rentInfoRepository.getAll()) {
                        getEndTimeForRent(rentInfo).ifPresent(time -> {
                            if (time.isBefore(LocalDateTime.now())) {
                                clearPlotRenter(rentInfo);
                            }
                        });
                    }
                })
                .submit(AtherysTowns.getInstance());
    }

    public Optional<LocalDateTime> getEndTimeForRent(RentInfo rentInfo) {
        if (rentInfo.getRenter() == null) {
            return Optional.empty();
        }

        Duration totalRent = rentInfo.getPeriod().multipliedBy(rentInfo.getPeriodsRented());
        return Optional.of(rentInfo.getTimeRented().plus(totalRent));
    }

    public void setPlotRentInfo(TownPlot plot, BigDecimal price, Duration period, TownsPermissionContext context) {
        RentInfo rentInfo = new RentInfo();
        rentInfo.setPeriod(period);
        rentInfo.setPrice(price);
        rentInfo.setPermissionContext(context);
        rentInfo.setPlot(plot);

        plot.setRentInfo(rentInfo);

        rentInfoRepository.saveOne(rentInfo);
        townPlotRepository.saveOne(plot);
    }

    public void setPlotRenter(RentInfo rentInfo, Resident resident, int periods) {
        rentInfo.setRenter(resident);
        rentInfo.setTimeRented(LocalDateTime.now());
        rentInfo.setPeriodsRented(periods);

        resident.addTenantPlot(rentInfo);

        rentInfoRepository.saveOne(rentInfo);
        residentRepository.saveOne(resident);
    }

    public void setRentPeriods(RentInfo rentInfo, int periods) {
        rentInfo.setPeriodsRented(periods);
        rentInfoRepository.saveOne(rentInfo);
    }

    public void clearPlotRenter(RentInfo rentInfo) {
        Resident renter = rentInfo.getRenter();

        rentInfo.setRenter(null);
        rentInfo.setPeriodsRented(0);
        rentInfo.setTimeRented(null);

        renter.removeTenantPlot(rentInfo);

        rentInfoRepository.saveOne(rentInfo);
        residentRepository.saveOne(renter);
    }

    public void clearPlotRentInfo(TownPlot plot) {
        RentInfo rentInfo = plot.getRentInfo().get();

        plot.setRentInfo(null);

        Resident resident = rentInfo.getRenter();
        if (resident != null) {
            resident.removeTenantPlot(rentInfo);
            residentRepository.saveOne(resident);
        }

        townPlotRepository.saveOne(plot);
        rentInfoRepository.deleteOne(rentInfo);
    }
}
