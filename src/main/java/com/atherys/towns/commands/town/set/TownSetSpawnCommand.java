package com.atherys.towns.commands.town.set;

import com.atherys.towns.commands.TownsSimpleCommand;
import com.atherys.towns.messaging.TownMessage;
import com.atherys.towns.nation.Nation;
import com.atherys.towns.permissions.actions.TownActions;
import com.atherys.towns.resident.Resident;
import com.atherys.towns.town.Town;
import javax.annotation.Nullable;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class TownSetSpawnCommand extends TownsSimpleCommand {

    private static TownSetSpawnCommand instance = new TownSetSpawnCommand();

    public static TownSetSpawnCommand getInstance() {
        return instance;
    }

    @Override
    protected CommandResult execute(Player player, CommandContext args, Resident resident,
        @Nullable Town town, @Nullable Nation nation) {
        if (town == null) {
            return CommandResult.empty();
        }

        Location<World> loc = player.getLocation();

        if (town.contains(loc)) {
            town.setSpawn(loc);
            town.informResidents(
                Text.of("Town Spawn changed to ", loc.getBlockPosition().toString()));
        } else {
            TownMessage.warn(player, "Town Spawn must be located within the borders of the town.");
        }

        return CommandResult.success();
    }

    @Override
    public CommandSpec getSpec() {
        return CommandSpec.builder()
            .description(Text.of("Used to change the spawn point of the town."))
            .permission(TownActions.SET_SPAWN.getPermission())
            .executor(this)
            .build();
    }
}
