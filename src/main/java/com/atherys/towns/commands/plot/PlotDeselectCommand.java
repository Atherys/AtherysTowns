package com.atherys.towns.commands.plot;

import com.atherys.core.command.annotation.Aliases;
import com.atherys.core.command.annotation.Description;
import com.atherys.towns.commands.TownsCommand;
import com.atherys.towns.commands.TownsValues;
import com.atherys.towns.messaging.TownMessage;
import com.atherys.towns.nation.Nation;
import com.atherys.towns.resident.Resident;
import com.atherys.towns.town.Town;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;

import javax.annotation.Nullable;

@Aliases("deselect")
@Description("Used to remove your current selection with the '/plot tool'.")
public class PlotDeselectCommand extends TownsCommand {

    @Override
    protected CommandResult execute(Player player, CommandContext args, Resident resident,
                                    @Nullable Town town, @Nullable Nation nation) {
        TownsValues.remove(player.getUniqueId(), TownsValues.TownsKey.PLOT_SELECTOR_1ST);
        TownsValues.remove(player.getUniqueId(), TownsValues.TownsKey.PLOT_SELECTOR_2ND);
        TownMessage.inform(player, "Deselected.");
        return CommandResult.success();
    }

}
