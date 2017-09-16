package com.atherys.towns.resident.ranks;

public enum NationRank {
    NONE        (0),
    RESIDENT    (1),
    CO_LEADER   (2),
    LEADER      (3);

    public enum Action implements TownsAction {
        NONE                        ("atherys.towns.actions.nation.none"),
        CREATE_NATION               ("atherys.towns.actions.nation.create"),
        SET_COLOR                   ("atherys.towns.actions.nation.set.color"),
        SET_NAME                    ("atherys.towns.actions.nation.set.name"),
        NATION_WITHDRAW             ("atherys.towns.actions.nation.withdraw"),
        NATION_DEPOSIT              ("atherys.towns.actions.nation.desposit");

        String permission;

        Action ( String permission ) { this.permission = permission; }

        @Override
        public String permission() { return permission; }

        @Override
        public boolean isNone() { return this == NONE; }
    }

    private int id;
    private static final NationRank[] values = NationRank.values();

    public int id() { return id; }

    NationRank ( int id ) { this.id = id; }

    public static NationRank fromId( int id ) {
        for ( NationRank r : values ) {
            if ( r.id() == id ) return r;
        }
        return NationRank.NONE;
    }
}
