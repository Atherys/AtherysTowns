package com.atherys.towns.commands.town.set;

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
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import javax.annotation.Nullable;

public class TownSetSpawnCommand extends AbstractTownSetCommand{

    TownSetSpawnCommand() {
        super(
                new String[] { "spawn" },
                "spawn",
                Text.of( "Used to change the town Spawn." ),
                TownRank.Action.SET_MOTD
        );
    }

    @Override
    public CommandResult townsExecute(@Nullable Nation nation, @Nullable Town town, Resident resident, Player player, CommandContext args) {
        if (town == null) return CommandResult.empty();

        Location<World> loc = player.getLocation();

        if (town.contains(loc)) {
            town.setSpawn(loc);
            town.informResidents(Text.of("Town Spawn changed to ", loc.getBlockPosition().toString() ));
        } else {
            TownMessage.warn(player, "Town Spawn must be located within the borders of the town.");
        }

        return CommandResult.success();
    }

    @Override
    public CommandSpec getSpec() {
        return CommandSpec.builder()
                .permission("atherys.towns.commands.town.set.spawn")
                .description(Text.of("Used to set the Spawn of the town."))
                .executor(this)
                .build();
    }

}
