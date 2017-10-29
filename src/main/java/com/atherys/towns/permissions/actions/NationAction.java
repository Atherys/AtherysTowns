package com.atherys.towns.permissions.actions;

public enum NationAction implements TownsAction {
    CHAT                        ("atherystowns.actions.nation.chat"),
    SET_COLOR                   ("atherystowns.actions.nation.set.color"),
    SET_NAME                    ("atherystowns.actions.nation.set.name"),
    SET_RANK                    ("atherystowns.actions.nation.set.rank"),
    SET_DESCRIPTION             ("atherystowns.actions.nation.set.description"),
    SET_LEADER_TITLE            ("atherystowns.actions.nation.set.leader_title"),
    NATION_WITHDRAW             ("atherystowns.actions.nation.withdraw"),
    NATION_DEPOSIT              ("atherystowns.actions.nation.deposit");

    String permission;

    NationAction ( String permission ) { this.permission = permission; }

    @Override
    public String getPermission() {
        return permission;
    }
}
