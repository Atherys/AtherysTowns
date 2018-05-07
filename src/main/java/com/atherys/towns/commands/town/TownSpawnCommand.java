package com.atherys.towns.commands.town;

import com.atherys.towns.AtherysTowns;
import com.atherys.towns.commands.TownsSimpleCommand;
import com.atherys.towns.messaging.TownMessage;
import com.atherys.towns.nation.Nation;
import com.atherys.towns.permissions.actions.TownActions;
import com.atherys.towns.resident.Resident;
import com.atherys.towns.town.Town;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class TownSpawnCommand extends TownsSimpleCommand {

    private static TownSpawnCommand instance = new TownSpawnCommand();

    public static TownSpawnCommand getInstance() {
        return instance;
    }

    @Override
    protected CommandResult execute(Player player, CommandContext args, Resident resident,
        @Nullable Town town, @Nullable Nation nation) {
        if (town == null) {
            return CommandResult.empty();
        }

        TownMessage.inform(player, "You will be teleported home in ",
            AtherysTowns.getConfig().TOWN.SPAWN_DELAY, " seconds.");

        Location<World> spawn = town.getSpawn();
        Task.builder()
            .delay(AtherysTowns.getConfig().TOWN.SPAWN_DELAY, TimeUnit.SECONDS)
            .execute(() -> {
                player.setLocationSafely(spawn);
                TownMessage.inform(player, "You have returned to your town's spawn!");
            })
            .name("atherystowns-spawn-task-" + player.getName())
            .submit(AtherysTowns.getInstance());

        return CommandResult.success();
    }

    @Override
    public CommandSpec getSpec() {
        return CommandSpec.builder()
            .description(Text.of(
                "Used to teleport to the spawn location of your town. Must be part of a town."))
            .permission(TownActions.TOWN_SPAWN.getPermission())
            .executor(this)
            .build();
    }
}
