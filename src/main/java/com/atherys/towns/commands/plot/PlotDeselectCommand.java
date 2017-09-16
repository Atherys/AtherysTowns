package com.atherys.towns.commands.plot;

import com.atherys.towns.commands.TownsValues;
import com.atherys.towns.messaging.TownMessage;
import com.atherys.towns.nation.Nation;
import com.atherys.towns.resident.Resident;
import com.atherys.towns.town.Town;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import javax.annotation.Nullable;

public class PlotDeselectCommand extends AbstractPlotCommand {

    PlotDeselectCommand() {
        super(
                new String[] {"desel", "deselect"},
                "desel",
                Text.of("Used to erase the current plot selector tool selection")
        );
    }

    @Override
    public CommandResult townsExecute(@Nullable Nation nation, @Nullable Town town, Resident resident, Player player, CommandContext args) {
        TownsValues.remove(player.getUniqueId(), TownsValues.TownsKey.PLOT_SELECTOR_1ST);
        TownsValues.remove(player.getUniqueId(), TownsValues.TownsKey.PLOT_SELECTOR_2ND);
        TownMessage.inform(player,               Text.of("Deselected.")                );
        return CommandResult.success();
    }

    @Override
    public CommandSpec getSpec() {
        return CommandSpec.builder()
                .permission("atherys.towns.commands.plot.deselect")
                .description(Text.of("Used to deselect your current plot selection."))
                .executor(this)
                .build();
    }
}
