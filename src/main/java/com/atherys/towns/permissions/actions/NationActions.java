package com.atherys.towns.permissions.actions;

public final class NationActions {

  public static final NationAction CHAT = new NationAction("chat", "Chat",
      "atherystowns.actions.nation.chat");

  public static final NationAction SET_COLOR = new NationAction("set_color", "Set Color",
      "atherystowns.actions.nation.set.color");

  public static final NationAction SET_NAME = new NationAction("set_name", "Set Name",
      "atherystowns.actions.nation.set.name");

  public static final NationAction SET_RANK = new NationAction("set_rank", "Set Rank",
      "atherystowns.actions.nation.set.rank");

  public static final NationAction SET_DESCRIPTION = new NationAction("set_description",
      "Set Description", "atherystowns.actions.nation.set.description");

  public static final NationAction SET_LEADER_TITLE = new NationAction("set_leader_title",
      "Set Leader Title", "atherystowns.actions.nation.set.leader_title");

  public static final NationAction NATION_WITHDRAW = new NationAction("nation_withdraw",
      "Nation Withdraw", "atherystowns.actions.nation.withdraw");

  public static final NationAction NATION_DEPOSIT = new NationAction("nation_deposit",
      "Nation Deposit", "atherystowns.actions.nation.deposit");

}
