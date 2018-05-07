package com.atherys.towns.commands.plot;

import com.atherys.core.command.annotation.Aliases;
import com.atherys.core.command.annotation.Children;
import com.atherys.core.command.annotation.Description;
import com.atherys.towns.commands.TownsCommand;
import com.atherys.towns.nation.Nation;
import com.atherys.towns.resident.Resident;
import com.atherys.towns.town.Town;
import javax.annotation.Nullable;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;

@Aliases({"plot", "p"})
@Description("Master Plot command.")
@Children({
    PlotToolCommand.class,
    PlotHereCommand.class,
    PlotSetFlagCommand.class,
    PlotSetNameCommand.class,
    PlotDeselectCommand.class
})
public class PlotMasterCommand extends TownsCommand {

  @Override
  protected CommandResult execute(Player player, CommandContext args, Resident resident,
      @Nullable Town town, @Nullable Nation nation) {
    //showHelp( "p", player );
    return CommandResult.empty();
  }
}
