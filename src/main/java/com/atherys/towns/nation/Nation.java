package com.atherys.towns.nation;

import com.atherys.towns.AtherysTowns;
import com.atherys.towns.api.nation.INation;
import com.atherys.towns.api.resident.IResident;
import com.atherys.towns.api.town.ITown;
import com.atherys.towns.views.NationView;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.world.World;

public class Nation implements INation {

    private UUID uuid;
    private NationMeta meta;

    private IResident leader;
    private ITown capital;
    private List<ITown> towns = new ArrayList<>();

    private double tax;

    protected Nation() {
        this.uuid = UUID.randomUUID();
        this.meta = new NationMeta();
    }

    protected Nation(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public UUID getUUID() {
        return uuid;
    }

    @Override
    public List<ITown> getTowns() {
        return towns;
    }

    @Override
    public Optional<ITown> getCapital() {
        return Optional.ofNullable(capital);
    }

    @Override
    public void setCapital(ITown town) {
        this.capital = town;
    }

    @Override
    public Optional<IResident> getLeader() {
        return Optional.ofNullable(leader);
    }

    @Override
    public void setLeader(IResident resident) {
        this.leader = resident;
    }

    @Override
    public double getTax() {
        return tax;
    }

    @Override
    public void setTax(double tax) {
        this.tax = tax;
    }

    @Override
    public NationView createView() {
        return new NationView(this);
    }

    @Override
    public boolean contains(World w, double x, double y) {
        for ( ITown town : getTowns() ) {
            if ( town.contains(w, x, y) ) return true;
        }
        return false;
    }

    @Override
    public Optional<UniqueAccount> getBankAccount() {
        return AtherysTowns.getInstance().getEconomyService().flatMap(service -> service.getOrCreateAccount(getUUID()));
    }

    @Override
    public NationMeta getMeta() {
        return meta;
    }

    @Override
    public Collection<IResident> getResidents() {
        List<IResident> residents = new ArrayList<>();
        getTowns().forEach(town -> residents.addAll(town.getResidents()));
        return residents;
    }
}
