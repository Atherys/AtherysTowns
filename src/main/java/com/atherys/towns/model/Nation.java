package com.atherys.towns.model;

import com.atherys.towns.model.entity.Resident;
import com.atherys.towns.model.entity.Town;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.text.Text;

import java.util.Objects;
import java.util.Set;

public class Nation {

    private String id;

    private Text name;

    private Text description;

    private Resident leader;

    private Town capital;

    private boolean joinable;

    private double tax;

    private Account bankAccount;

    public Nation(String id,
                  Text name,
                  Text description,
                  Resident leader,
                  Town capital,
                  boolean joinable,
                  double tax,
                  Account bankAccount
    ) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.leader = leader;
        this.capital = capital;
        this.joinable = joinable;
        this.tax = tax;
        this.bankAccount = bankAccount;
    }

    public String getId() {
        return id;
    }

    public Text getName() {
        return name;
    }

    public Text getDescription() {
        return description;
    }

    public Resident getLeader() {
        return leader;
    }

    public Town getCapital() {
        return capital;
    }

    public boolean isJoinable() {
        return joinable;
    }

    public double getTax() {
        return tax;
    }

    public Account getBankAccount() {
        return bankAccount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Nation nation = (Nation) o;
        return Objects.equals(id, nation.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
