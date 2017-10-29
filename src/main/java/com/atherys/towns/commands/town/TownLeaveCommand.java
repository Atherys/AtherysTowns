package com.atherys.towns.commands.town;

import com.atherys.towns.commands.TownsSimpleCommand;
import com.atherys.towns.nation.Nation;
import com.atherys.towns.permissions.actions.TownAction;
import com.atherys.towns.resident.Resident;
import com.atherys.towns.town.Town;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import javax.annotation.Nullable;

public class TownLeaveCommand extends TownsSimpleCommand {

    private static TownLeaveCommand instance = new TownLeaveCommand();

    public static TownLeaveCommand getInstance() {
        return instance;
    }

    @Override
    protected CommandResult execute(Player player, CommandContext args, Resident resident, @Nullable Town town, @Nullable Nation nation) {

        resident.leaveTown();

        return CommandResult.success();
    }

    @Override
    public CommandSpec getSpec() {
        return CommandSpec.builder()
                .description( Text.of( "Used to leave a town. If you are not part of a town, this command will not function." ) )
                .permission( TownAction.LEAVE_TOWN.getPermission() )
                .executor( new TownLeaveCommand() )
                .build();
    }
}
