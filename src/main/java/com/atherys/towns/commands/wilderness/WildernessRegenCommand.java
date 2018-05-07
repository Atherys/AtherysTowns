package com.atherys.towns.commands.wilderness;

import com.atherys.core.command.annotation.Aliases;
import com.atherys.core.command.annotation.Description;
import com.atherys.core.command.annotation.Permission;
import com.atherys.towns.managers.WildernessManager;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;

@Aliases("wregen")
@Description("Wilderness forced regeneration command.")
@Permission("atherystowns.admin.wilderness.regen")
public class WildernessRegenCommand implements CommandExecutor {

  @Override
  public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
    long timestamp = System.currentTimeMillis();
    WildernessManager.getInstance().regenerate(timestamp);
    return CommandResult.success();
  }
}
