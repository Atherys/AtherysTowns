package com.atherys.towns.town;

public enum TownStatus {
    NONE(0),
    TOWN(1),
    CAPITAL(2);

    int id;

    TownStatus(int id) {
        this.id = id;
    }

    public int id() {
        return id;
    }

    public static TownStatus fromId(int id) {
        for (TownStatus ts : TownStatus.values()) {
            if (ts.id == id) {
                return ts;
            }
        }
        return NONE;
    }
}
