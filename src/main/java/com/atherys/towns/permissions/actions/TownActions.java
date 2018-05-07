package com.atherys.towns.permissions.actions;

public final class TownActions {

  public static final TownAction CREATE_NATION = new TownAction("create_nation", "Create Nation",
      "atherystowns.actions.nation.create");

  public static final TownAction CREATE_TOWN = new TownAction("create_town", "Create Town",
      "atherystowns.actions.town.create");

  public static final TownAction JOIN_TOWN = new TownAction("join_town", "Join Town",
      "atherystowns.actions.town.join");

  public static final TownAction LEAVE_TOWN = new TownAction("leave_town", "Leave Town",
      "atherystowns.actions.town.leave");

  public static final TownAction INVITE_PLAYER = new TownAction("invite_player", "Invite Player",
      "atherystowns.actions.town.invite");

  public static final TownAction KICK_PLAYER = new TownAction("kick_player", "Kick Player",
      "atherystowns.actions.town.kick");

  public static final TownAction CLAIM_PLOT = new TownAction("claim_plot", "Claim Plot",
      "atherystowns.actions.town.claim");

  public static final TownAction UNCLAIM_PLOT = new TownAction("unclaim_plot", "Unclaim Plot",
      "atherystowns.actions.town.unclaim");

  public static final TownAction SET_NAME = new TownAction("set_name", "Set Town Name",
      "atherystowns.actions.town.set.name");

  public static final TownAction SET_MOTD = new TownAction("set_motd", "Set Town MOTD",
      "atherystowns.actions.town.set.motd");

  public static final TownAction SET_DESCRIPTION = new TownAction("set_description",
      "Set Town Description", "atherystowns.actions.town.set.description");

  public static final TownAction SET_RANK = new TownAction("set_rank", "Set Rank",
      "atherystowns.actions.town.set.rank");

  public static final TownAction SET_SPAWN = new TownAction("set_spawn", "Set Spawn",
      "atherystowns.actions.town.set.spawn");

  public static final TownAction SET_MAYOR = new TownAction("set_mayor", "Set Mayor",
      "atherystowns.actions.town.set.mayor");

  public static final TownAction SET_COLOR = new TownAction("set_color", "Set Color",
      "atherystowns.actions.town.set.color");

  public static final TownAction SET_NATION = new TownAction("set_nation", "Set Nation",
      "atherystowns.actions.town.set.nation");

  public static final TownAction SET_FLAGS = new TownAction("set_flags", "Set Flags",
      "atherystowns.actions.town.set.flag");

  public static final TownAction SET_FLAG_PVP = new TownAction("set_pvp", "Set Flag: PVP",
      "atherystowns.actions.town.set.flag.pvp");

  public static final TownAction SET_FLAG_BUILD = new TownAction("set_build", "Set Flag: Build",
      "atherystowns.actions.town.set.flag.build");

  public static final TownAction SET_FLAG_DESTROY = new TownAction("set_destroy",
      "Set Flag: Destroy", "atherystowns.actions.town.set.flag.destroy");

  public static final TownAction SET_FLAG_SWITCH = new TownAction("set_switch",
      "Set Flag: Switch", "atherystowns.actions.town.set.flag.switch");

  public static final TownAction SET_FLAG_DAMAGE_ENTITY = new TownAction("set_damage_entity",
      "Set Flag: Damage Entity", "atherystowns.actions.town.set.flag.damage_entity");

  public static final TownAction SET_FLAG_JOIN = new TownAction("set_join", "Set Flag: Join",
      "atherystowns.actions.town.set.flag.join");

  public static final TownAction CHAT = new TownAction("chat", "Chat",
      "atherystowns.actions.town.chat");

  public static final TownAction RUIN_TOWN = new TownAction("ruin_town", "Ruin Town",
      "atherystowns.actions.town.ruin");

  public static final TownAction SHOW_TOWN_BORDER = new TownAction("town_border",
      "Show Town Border", "atherystowns.actions.town.border");

  public static final TownAction TOWN_DEPOSIT = new TownAction("town_deposit", "Town Deposit",
      "atherystowns.actions.town.deposit");

  public static final TownAction TOWN_WITHDRAW = new TownAction("town_withdraw", "Town Withdraw",
      "atherystowns.actions.town.withdraw");

  public static final TownAction TOWN_SPAWN = new TownAction("town_spawn", "Town Spawn",
      "atherystowns.actions.town.spawn");

  public static final TownAction MODIFY_PLOT_NAME = new TownAction("plot_name",
      "Modify Plot Name", "atherystowns.actions.plot.name");

  public static final TownAction MODIFY_PLOT_FLAG = new TownAction("plot_flag",
      "Modify Flag Name", "atherystowns.actions.plot.flag");
}
