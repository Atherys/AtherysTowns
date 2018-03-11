package com.atherys.towns.commands.town;

import com.atherys.towns.commands.TownsSimpleCommand;
import com.atherys.towns.managers.TownManager;
import com.atherys.towns.messaging.TownMessage;
import com.atherys.towns.nation.Nation;
import com.atherys.towns.permissions.actions.TownActions;
import com.atherys.towns.plot.flags.Extents;
import com.atherys.towns.plot.flags.Flags;
import com.atherys.towns.resident.Resident;
import com.atherys.towns.town.Town;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

public class TownJoinCommand extends TownsSimpleCommand {

    private static TownJoinCommand instance = new TownJoinCommand();

    public static TownJoinCommand getInstance() {
        return instance;
    }

    @Override
    protected CommandResult execute(Player player, CommandContext args, Resident resident, @Nullable Town town, @Nullable Nation nation) {
        Optional<Town> tOpt = TownManager.getInstance().getFirstByName(args.<String>getOne("townName").orElse(UUID.randomUUID().toString()));

        if ( !tOpt.isPresent() ) {
            TownMessage.warn(player, "That town doesn't exist!");
            return CommandResult.empty();
        } else {

            if ( town != null ) {
                TownMessage.warn(player, "You are already part of a town!");
                return CommandResult.empty();
            }

            Town t = tOpt.get();

            if ( t.getTownFlags().get( Flags.JOIN ) == Extents.ANY ) {
                TownInviteCommand.getInstance().inviteResident( resident, t );
            } else {
                TownMessage.warn(player, "The town you are trying to join requires an invitation.");
                return CommandResult.empty();
            }
        }

        return CommandResult.empty();
    }

    @Override
    public CommandSpec getSpec() {
        return CommandSpec.builder()
                .description( Text.of( "Used to join a town. If you are already part of a town, you must leave your current town first." ) )
                .permission( TownActions.JOIN_TOWN.getPermission() )
                .arguments(
                        GenericArguments.remainingJoinedStrings(Text.of("townName"))
                )
                .executor( this )
                .build();
    }
}
