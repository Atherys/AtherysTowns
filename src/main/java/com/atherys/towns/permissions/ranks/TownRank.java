package com.atherys.towns.permissions.ranks;

import com.atherys.towns.permissions.actions.TownAction;

import java.util.List;

public class TownRank {

    private int id;
    private String name;
    private NationRank defaultNationRank;
    private List<TownAction> permittedActions;

}
