package com.atherys.towns.resident.ranks;

import com.atherys.towns.plot.PlotFlags;

public enum TownRank {
    NONE        (0, "None"      ),
    RESIDENT    (1, "Resident"  ),
    CITIZEN     (2, "Citizen"   ),
    ASSISTANT   (3, "Assistant" ),
    CO_MAYOR    (4, "Co-Mayor"  ),
    MAYOR       (5, "Mayor"     );

    public enum Action implements TownsAction {
        ALL                         ("atherys.towns.actions.all"),
        NULL                        ("atherys.towns.actions.null"),
        NONE                        ("atherys.towns.actions.none"),
        JOIN_TOWN                   ("atherys.towns.actions.town.join"),
        LEAVE_TOWN                  ("atherys.towns.actions.town.leave"),
        INVITE_PLAYER               ("atherys.towns.actions.town.invite"),
        KICK_PLAYER                 ("atherys.towns.actions.town.kick"),
        CLAIM_PLOT                  ("atherys.towns.actions.town.claim"),
        UNCLAIM_PLOT                ("atherys.towns.actions.town.unclaim"),
        SET_NAME                    ("atherys.towns.actions.town.set.name"),
        SET_MOTD                    ("atherys.towns.actions.town.set.motd"),
        SET_DESCRIPTION             ("atherys.towns.actions.town.set.description"),
        SET_RANK                    ("atherys.towns.actions.town.set.rank"),
        SET_MAYOR                   ("atherys.towns.actions.town.set.mayor"),
        SET_COLOR                   ("atherys.towns.actions.town.set.color"),
        SET_NATION                  ("atherys.towns.actions.town.set.nation"),
        SET_FLAG_PVP                ("atherys.towns.actions.town.set.flag.pvp"),
        SET_FLAG_BUILD              ("atherys.towns.actions.town.set.flag.build"),
        SET_FLAG_DESTROY            ("atherys.towns.actions.town.set.flag.destroy"),
        SET_FLAG_SWITCH             ("atherys.towns.actions.town.set.flag.switch"),
        SET_FLAG_DAMAGE_ENTITY      ("atherys.towns.actions.town.set.flag.damage_entity"),
        SET_FLAG_JOIN               ("atherys.towns.actions.town.set.join"),
        SET_FLAGS                   ("atherys.towns.actions.town.set.flags"),
        CHAT                        ("atherys.towns.actions.town.chat"),
        CREATE_NATION               ("atherys.towns.actions.nation.create"),
        RUIN_TOWN                   ("atherys.towns.actions.town.ruin"),
        SHOW_TOWN_BORDER            ("atherys.towns.actions.town.border"),
        TOWN_DEPOSIT                ("atherys.towns.actions.town.deposit"),
        TOWN_WITHDRAW               ("atherys.towns.actions.town.withdraw"),
        NATION_WITHDRAW             ("atherys.towns.actions.nation.withdraw"),
        NATION_DEPOSIT              ("atherys.towns.actions.nation.desposit"),
        TOWN_SPAWN                  ("atherys.towns.actions.town.spawn"),
        MODIFY_PLOT_FLAG            ("atherys.towns.actions.plot.modifyflag"),
        MODIFY_PLOT_NAME            ("atherys.towns.actions.plot.modifyname");

        String permission;

        Action ( String permission ) { this.permission = permission; }

        @Override
        public String permission() { return permission; }

        @Override
        public boolean isNone() { return this == NONE; }

        public static Action fromFlag (PlotFlags.Flag flag) {
            switch ( flag ) {
                case PVP:
                    return SET_FLAG_PVP;
                case BUILD:
                    return SET_FLAG_BUILD;
                case SWITCH:
                    return SET_FLAG_SWITCH;
                case DESTROY:
                    return SET_FLAG_DESTROY;
                case DAMAGE_ENTITY:
                    return SET_FLAG_DAMAGE_ENTITY;
                case JOIN:
                    return SET_FLAG_JOIN;
                default:
                    return NONE;
            }
        }
    }

    private static final TownRank values[] = TownRank.values();

    int id;
    String formattedName;

    TownRank ( int id, String name ) {
        this.id = id;
        this.formattedName = name;
    }

    public String formattedName() {
        return formattedName;
    }

    public int id() { return id; }

    public static TownRank fromId ( int id ) {
        for ( TownRank rank : values ) if ( rank.id == id ) return rank;
        return NONE;
    }

    public boolean isAssistant() {
        return this.id > ASSISTANT.id;
    }

    public boolean isCoMayor() {
        return this.id > CO_MAYOR.id;
    }
}
