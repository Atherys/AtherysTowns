package com.atherys.towns.commands.town;

import com.atherys.towns.AtherysTowns;
import com.atherys.towns.Settings;
import com.atherys.towns.messaging.TownMessage;
import com.atherys.towns.nation.Nation;
import com.atherys.towns.resident.Resident;
import com.atherys.towns.resident.ranks.TownRank;
import com.atherys.towns.town.Town;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;

import javax.annotation.Nullable;
import java.util.concurrent.TimeUnit;

public class TownSpawnCommand extends AbstractTownCommand {

    TownSpawnCommand () {
        super(
                new String[] { "spawn", "home" },
                "spawn",
                Text.of("Used to get to your town's home point."),
                TownRank.Action.TOWN_SPAWN,
                true,
                false,
                true,
                true
        );
    }

    @Override
    public CommandResult townsExecute(@Nullable Nation nation, @Nullable Town town, Resident resident, Player player, CommandContext args) {
        if ( town == null ) return CommandResult.empty();

        TownMessage.inform( player, "You will be teleported home in ", Settings.TOWN_SPAWN_DELAY, " seconds.");

        Location spawn = town.spawn();
        Task.builder()
                .delay(Settings.TOWN_SPAWN_DELAY, TimeUnit.SECONDS)
                .execute( () -> {
                    player.setLocationSafely( spawn );
                    TownMessage.inform(player, "You have returned to your town's spawn!");
                })
                .name("atherystowns-spawn-task-" + player.getName())
                .submit(AtherysTowns.getInstance());

        return CommandResult.success();
    }

    @Override
    public CommandSpec getSpec() {
        return  CommandSpec.builder()
                .permission("atherys.town.commands.town.spawn")
                .description( Text.of("Used to spawn at your town's home point") )
                .executor(this)
                .build();
    }
}
