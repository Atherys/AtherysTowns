package com.atherys.towns.managers;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class RankManager {

    private final static RankManager instance = new RankManager();

    private List<TownRank2> townRanks = new ArrayList<>();
    private List<NationRank2> nationRanks = new ArrayList<>();

    private RankManager() {}

    public void load() {

    }

    public void addNationRank ( NationRank2 rank ) {
        if ( this.nationRanks.contains( rank ) ) {
            this.nationRanks.add(rank);
        }
    }

    public void addTownRank ( TownRank2 rank ) {
        if ( this.townRanks.contains( rank ) ) {
            this.townRanks.add(rank);
        }
    }

    public Optional<TownRank2> getTownRankByName (String name ) {
        for ( TownRank2 rank : townRanks ) {
            if ( rank.getName().equals(name) ) return Optional.of(rank);
        }
        return Optional.empty();
    }

    public Optional<NationRank2> getNationRankByName ( String name ) {
        for ( NationRank2 rank : nationRanks ) {
            if ( rank.getName().equals(name) ) return Optional.of(rank);
        }
        return Optional.empty();
    }

    public Optional<NationRank2> getNationRankById (int id ) {
        for ( NationRank2 rank : nationRanks ) {
            if ( rank.getId() == id ) return Optional.of(rank);
        }
        return Optional.empty();
    }

    public Optional<TownRank2> getTownRankById (int id ) {
        for ( TownRank2 rank : townRanks ) {
            if ( rank.getId() == id ) return Optional.of(rank);
        }
        return Optional.empty();
    }

    public static RankManager getInstance() {
        return instance;
    }

    public void fromJson(JsonObject object) {

    }
}
