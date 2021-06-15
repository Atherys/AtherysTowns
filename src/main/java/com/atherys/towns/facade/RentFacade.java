package com.atherys.towns.facade;

import com.atherys.core.economy.Economy;
import com.atherys.core.utils.TextUtils;
import com.atherys.towns.TownsConfig;
import com.atherys.towns.api.command.TownsCommandException;
import com.atherys.towns.api.permission.TownsPermissionContext;
import com.atherys.towns.model.entity.RentInfo;
import com.atherys.towns.model.entity.Resident;
import com.atherys.towns.model.entity.TownPlot;
import com.atherys.towns.service.PlotService;
import com.atherys.towns.service.RentService;
import com.atherys.towns.service.ResidentService;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.economy.transaction.TransferResult;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.spongepowered.api.text.action.TextActions.showText;
import static org.spongepowered.api.text.format.TextColors.*;

@Singleton
public class RentFacade implements EconomyFacade {
    @Inject
    private PlotFacade plotFacade;

    @Inject
    private ResidentFacade residentFacade;

    @Inject
    private TownFacade townFacade;

    @Inject
    private TownsMessagingFacade townsMsg;

    @Inject
    private ResidentService residentService;

    @Inject
    private RentService rentService;

    @Inject
    private PlotService plotService;

    @Inject
    private TownsConfig config;

    public void makePlotRentable(Player source, BigDecimal price, Duration rentPeriod, TownsPermissionContext context) throws TownsCommandException {
        TownPlot plot = plotFacade.getPlotAtPlayer(source);

        if (plot.getRentInfo().isPresent() && plot.getRentInfo().get().getRenter() != null) {
            throw new TownsCommandException("Someone is renting this plot.");
        }

        if (config.TOWN.RENT_CONFIG.MINIMUM_RENT_COST > price.doubleValue()) {
            throw new TownsCommandException("Rent price is below the minimum.");
        }

        if (config.TOWN.RENT_CONFIG.MAXIMUM_RENT_COST < price.doubleValue()) {
            throw new TownsCommandException("Rent price is above the maximum.");
        }

        if (rentPeriod.compareTo(config.TOWN.RENT_CONFIG.MINIMUM_RENT_PERIOD) < 0) {
            throw new TownsCommandException("Rent period is below the minimum.");
        }

        if (rentPeriod.compareTo(config.TOWN.RENT_CONFIG.MAXIMUM_RENT_PERIOD) > 0) {
            throw new TownsCommandException("Rent period is above the maximum.");
        }

        rentService.setPlotRentInfo(plot, price, rentPeriod, context);
        townsMsg.info(source, "Plot made rentable.");
    }

    public void clearPlotRentable(Player source) throws TownsCommandException {
        TownPlot plot = plotFacade.getPlotAtPlayer(source);
        getRentInfoFromPlot(plot);

        rentService.clearPlotRentInfo(plot);
        townsMsg.info(source, "Plot no longer rented.");
    }

    public void sendRentInfo(Player source) throws TownsCommandException {
        TownPlot plot = plotFacade.getPlotAtPlayer(source);
        RentInfo rentInfo = getRentInfoFromPlot(plot);

        Text.Builder rentText = Text.builder();
        rentText.append(townsMsg.createTownsHeader(plot.getName().toPlain()));

        if (rentInfo.getRenter() == null) {
            rentText.append(Text.of(DARK_GREEN, "Rent Cost: ", GOLD, config.DEFAULT_CURRENCY.format(rentInfo.getPrice()), Text.NEW_LINE));
            rentText.append(Text.of(DARK_GREEN, "Rent For: ", GOLD, TextUtils.formatDuration(rentInfo.getPeriod().toMillis())));
        } else {
            rentText.append(Text.of(DARK_GREEN, "Rented By: ", residentFacade.renderResident(rentInfo.getRenter()), Text.NEW_LINE));
            rentText.append(Text.of(DARK_GREEN, "Rented For: ", GOLD, formatRentDuration(rentInfo)));
        }

        source.sendMessage(rentText.build());
    }

    public void listRentInfo(Player source) {
        Resident resident = residentService.getOrCreate(source);

        if (resident.getTenantPlots().isEmpty()) {
            townsMsg.info(source, "You are not renting any plots.");
            return;
        }

        List<Text> rentList = resident.getTenantPlots().stream()
                .map(rentInfo -> {
                    TownPlot plot = rentInfo.getPlot();
                    Text plotText = Text.builder()
                            .append(Text.of(GOLD, plot.getName(), DARK_GREEN, " rented for ", GOLD, formatRentDuration(rentInfo)))
                            .onHover(showText(Text.of(
                                    DARK_GREEN, "Town: ", GOLD, plot.getTown().getName(), Text.NEW_LINE,
                                    DARK_GREEN, "Location: ", GOLD, plot.getSouthWestCorner()
                            )))
                            .build();
                    return Text.of(plotText);
                })
                .collect(Collectors.toList());

        PaginationList paginationList = PaginationList.builder()
                .title(Text.of(GOLD, "Rented Plots"))
                .padding(Text.of(DARK_GRAY, "="))
                .contents(rentList)
                .linesPerPage(5)
                .build();

        paginationList.sendTo(source);
    }

    private Text formatRentDuration(RentInfo rentInfo) {
        Duration timeLeft = Duration.between(LocalDateTime.now(), rentService.getEndTimeForRent(rentInfo).get());
        return TextUtils.formatDuration(timeLeft.toMillis());
    }

    private RentInfo getRentInfoFromPlot(TownPlot plot) throws TownsCommandException {
        return plot.getRentInfo().orElseThrow(() ->
                new TownsCommandException("The plot you are standing on is not rentable.")
        );
    }

    public void purchaseRent(Player source, int periods) throws TownsCommandException {
        TownPlot plot = plotFacade.getPlotAtPlayer(source);
        RentInfo rentInfo = getRentInfoFromPlot(plot);
        Resident resident = residentService.getOrCreate(source);

        if (resident.getTenantPlots().size() >= config.TOWN.RENT_CONFIG.PLOTS_RENTABLE) {
            throw new TownsCommandException("You are not allowed to rent any more plots!");
        }

        // Someone else is renting this plot
        if (rentInfo.getRenter() != null && rentInfo.getRenter() != resident) {
            throw new TownsCommandException("This plot is already being rented!");
        }

        // Player is not allowed to rent this plot
        if (!plotFacade.getRelevantResidentContexts(plot, resident).contains(rentInfo.getPermissionContext())) {
            throw new TownsCommandException("You are not allowed to rent this plot.");
        }

        BigDecimal totalPrice = rentInfo.getPrice().multiply(BigDecimal.valueOf(periods));

        TransferResult paymentResult;

        if (plot.getOwner() == null) {
            paymentResult = townFacade.depositToTown(source, plot.getTown(), totalPrice);
        } else {
            paymentResult = Economy.transferCurrency(
                    source.getUniqueId(),
                    plot.getOwner().getId(),
                    config.DEFAULT_CURRENCY,
                    totalPrice,
                    Sponge.getCauseStackManager().getCurrentCause()
            ).orElseThrow(() ->
                    new TownsCommandException("Transaction failed. Please report this.")
            );
        }

        Text feedback = getResultFeedback(
                paymentResult.getResult(),
                Text.of("Plot rented."),
                Text.of("You do not have enough to rent this plot."),
                Text.of("Rent failed.")
        );

        if (rentInfo.getRenter() == resident) {
            rentService.setRentPeriods(rentInfo, rentInfo.getPeriodsRented() + periods);
        } else {
            rentService.setPlotRenter(rentInfo, resident, periods);
        }

        townsMsg.info(source, feedback);
    }

    public void evictPlotRenter(Player source) throws TownsCommandException {
        TownPlot plot = plotFacade.getPlotAtPlayer(source);
        RentInfo rentInfo = getRentInfoFromPlot(plot);

        rentService.clearPlotRenter(rentInfo);
        townsMsg.info(source, "Plot has been evicted.");
    }

    public void vacatePlot(Player source) throws TownsCommandException {
        TownPlot plot = plotFacade.getPlotAtPlayer(source);
        RentInfo rentInfo = getRentInfoFromPlot(plot);

        if (rentInfo.getRenter() == residentService.getOrCreate(source)) {
            rentService.clearPlotRenter(rentInfo);
            townsMsg.info(source, "You have vacated this plot.");
        } else {
            throw new TownsCommandException("You are not renting this plot.");
        }
    }

    public void updateRentOwnership(Resident resident) {
        Set<RentInfo> rents = resident.getTenantPlots();
        if (rents.isEmpty()) return;

        for (RentInfo rentInfo : rents) {
            if (!plotFacade.getRelevantResidentContexts(rentInfo.getPlot(), resident).contains(rentInfo.getPermissionContext())) {
                rentService.clearPlotRenter(rentInfo);
            }
        }
    }
}
