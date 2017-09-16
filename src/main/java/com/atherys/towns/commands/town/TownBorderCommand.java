package com.atherys.towns.commands.town;

import com.atherys.towns.commands.TownsValues;
import com.atherys.towns.messaging.TownMessage;
import com.atherys.towns.nation.Nation;
import com.atherys.towns.resident.Resident;
import com.atherys.towns.resident.ranks.TownRank;
import com.atherys.towns.town.Town;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import javax.annotation.Nullable;

public class TownBorderCommand extends AbstractTownCommand {

    TownBorderCommand () {
        super(
                new String[] { "border" },
                "border",
                Text.of("Used to see the plot borders of your town."),
                TownRank.Action.SHOW_TOWN_BORDER,
                true,
                false,
                true,
                true
        );
    }

    @Override
    public CommandResult townsExecute(@Nullable Nation nation, @Nullable Town town, Resident resident, Player player, CommandContext args) {
        if ( !TownsValues.get( player.getUniqueId(), TownsValues.TownsKey.TOWN_BORDERS ).isPresent() ) {
            TownsValues.set( player.getUniqueId(), TownsValues.TownsKey.TOWN_BORDERS, true );
            TownMessage.inform( player, "Now showing town borders.");
        } else {
            TownsValues.remove( player.getUniqueId(), TownsValues.TownsKey.TOWN_BORDERS);
            TownMessage.warn( player, "No longer showing town borders.");
        }

        return CommandResult.success();
    }

    @Override
    public CommandSpec getSpec() {
        return  CommandSpec.builder()
                .permission("atherys.town.commands.town.border")
                .description( Text.of("Used to see the borders of a town.") )
                .executor(this)
                .build();
    }
}
