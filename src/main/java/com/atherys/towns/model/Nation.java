package com.atherys.towns.model;

import com.atherys.towns.model.entity.Resident;
import com.atherys.towns.model.entity.Town;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.text.Text;

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

    private String nationLeaderRole;

    private String defaultNationRole;

    private Set<String> roles;

    public Nation(String id,
                  Text name,
                  Text description,
                  Resident leader,
                  Town capital,
                  boolean joinable,
                  double tax,
                  Account bankAccount,
                  String nationLeaderRole,
                  String defaultNationRole,
                  Set<String> roles
    ) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.leader = leader;
        this.capital = capital;
        this.joinable = joinable;
        this.tax = tax;
        this.bankAccount = bankAccount;
        this.nationLeaderRole = nationLeaderRole;
        this.defaultNationRole = defaultNationRole;
        this.roles = roles;
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

    public Set<String> getRoles() {
        return roles;
    }

    public String getNationLeaderRole() {
        return nationLeaderRole;
    }

    public String getDefaultNationRole() {
        return defaultNationRole;
    }
}
