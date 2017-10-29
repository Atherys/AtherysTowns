package com.atherys.towns.permissions.actions;

public enum TownAction implements TownsAction {
    CREATE_TOWN                 ("atherystowns.actions.town.create"),
    JOIN_TOWN                   ("atherystowns.actions.town.join"),
    LEAVE_TOWN                  ("atherystowns.actions.town.leave"),
    INVITE_PLAYER               ("atherystowns.actions.town.invite"),
    KICK_PLAYER                 ("atherystowns.actions.town.kick"),
    CLAIM_PLOT                  ("atherystowns.actions.town.claim"),
    UNCLAIM_PLOT                ("atherystowns.actions.town.unclaim"),
    SET_NAME                    ("atherystowns.actions.town.set.name"),
    SET_MOTD                    ("atherystowns.actions.town.set.motd"),
    SET_DESCRIPTION             ("atherystowns.actions.town.set.description"),
    SET_RANK                    ("atherystowns.actions.town.set.rank"),
    SET_SPAWN                   ("atherystowns.actions.town.set.spawn"),
    SET_MAYOR                   ("atherystowns.actions.town.set.mayor"),
    SET_COLOR                   ("atherystowns.actions.town.set.color"),
    SET_NATION                  ("atherystowns.actions.town.set.nation"),
    SET_FLAG_PVP                ("atherystowns.actions.town.set.flag.pvp"),
    SET_FLAG_BUILD              ("atherystowns.actions.town.set.flag.build"),
    SET_FLAG_DESTROY            ("atherystowns.actions.town.set.flag.destroy"),
    SET_FLAG_SWITCH             ("atherystowns.actions.town.set.flag.switch"),
    SET_FLAG_DAMAGE_ENTITY      ("atherystowns.actions.town.set.flag.damage_entity"),
    SET_FLAG_JOIN               ("atherystowns.actions.town.set.join"),
    SET_FLAGS                   ("atherystowns.actions.town.set.flags"),
    CHAT                        ("atherystowns.actions.town.chat"),
    CREATE_NATION               ("atherystowns.actions.nation.create"),
    RUIN_TOWN                   ("atherystowns.actions.town.ruin"),
    SHOW_TOWN_BORDER            ("atherystowns.actions.town.border"),
    TOWN_DEPOSIT                ("atherystowns.actions.town.deposit"),
    TOWN_WITHDRAW               ("atherystowns.actions.town.withdraw"),
    TOWN_SPAWN                  ("atherystowns.actions.town.spawn"),
    MODIFY_PLOT_FLAG            ("atherystowns.actions.plot.modify.flag"),
    MODIFY_PLOT_NAME            ("atherystowns.actions.plot.modify.name");

    String permission;

    TownAction ( String permission ) { this.permission = permission; }

    @Override
    public String getPermission() {
        return permission;
    }
}
