package com.atherys.towns.commands.plot;

import com.atherys.towns.commands.TownsSimpleCommand;
import com.atherys.towns.commands.TownsValues;
import com.atherys.towns.messaging.TownMessage;
import com.atherys.towns.nation.Nation;
import com.atherys.towns.resident.Resident;
import com.atherys.towns.town.Town;
import javax.annotation.Nullable;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

public class PlotDeselectCommand extends TownsSimpleCommand {

    private static PlotDeselectCommand instance = new PlotDeselectCommand();

    public static PlotDeselectCommand getInstance() {
        return instance;
    }

    @Override
    protected CommandResult execute(Player player, CommandContext args, Resident resident,
        @Nullable Town town, @Nullable Nation nation) {
        TownsValues.remove(player.getUniqueId(), TownsValues.TownsKey.PLOT_SELECTOR_1ST);
        TownsValues.remove(player.getUniqueId(), TownsValues.TownsKey.PLOT_SELECTOR_2ND);
        TownMessage.inform(player, Text.of("Deselected."));
        return CommandResult.success();
    }

    @Override
    public CommandSpec getSpec() {
        return CommandSpec.builder()
            .description(
                Text.of("Used to deselect your current plot definition selection ( if any )."))
            .executor(this)
            .build();
    }

}
