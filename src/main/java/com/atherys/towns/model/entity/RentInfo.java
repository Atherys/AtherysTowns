package com.atherys.towns.model.entity;


import com.atherys.core.db.Identifiable;
import com.atherys.core.db.converter.DurationConverter;
import com.atherys.towns.api.permission.TownsPermissionContext;
import com.atherys.towns.persistence.converter.TownPermissionContextConverter;

import javax.annotation.Nonnull;
import javax.persistence.*;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;

@Entity
@Table(schema = "atherystowns", name = "RentInfo")
public class RentInfo implements Identifiable<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Convert(converter = DurationConverter.class)
    private Duration period;

    private BigDecimal price;

    @Convert(converter = TownPermissionContextConverter.class)
    private TownsPermissionContext permissionContext;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private Resident renter;

    private LocalDateTime timeRented;

    private int periodsRented;

    @OneToOne
    private TownPlot plot;

    @Nonnull
    @Override
    public Long getId() {
        return id;
    }

    public Resident getRenter() {
        return renter;
    }

    public void setRenter(Resident renter) {
        this.renter = renter;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public LocalDateTime getTimeRented() {
        return timeRented;
    }

    public void setTimeRented(LocalDateTime timeRented) {
        this.timeRented = timeRented;
    }

    public Duration getPeriod() {
        return period;
    }

    public void setPeriod(Duration period) {
        this.period = period;
    }

    public TownsPermissionContext getPermissionContext() {
        return permissionContext;
    }

    public void setPermissionContext(TownsPermissionContext permissionContext) {
        this.permissionContext = permissionContext;
    }

    public int getPeriodsRented() {
        return periodsRented;
    }

    public void setPeriodsRented(int periodsRented) {
        this.periodsRented = periodsRented;
    }

    public TownPlot getPlot() {
        return plot;
    }

    public void setPlot(TownPlot plot) {
        this.plot = plot;
    }
}
