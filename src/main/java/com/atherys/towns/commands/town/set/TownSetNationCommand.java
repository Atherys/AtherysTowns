package com.atherys.towns.commands.town.set;

import com.atherys.towns.commands.TownsSimpleCommand;
import com.atherys.towns.managers.NationManager;
import com.atherys.towns.messaging.TownMessage;
import com.atherys.towns.nation.Nation;
import com.atherys.towns.permissions.actions.TownActions;
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

public class TownSetNationCommand extends TownsSimpleCommand {

    private static TownSetNationCommand instance = new TownSetNationCommand();

    public static TownSetNationCommand getInstance() {
        return instance;
    }

    @Override
    protected CommandResult execute(Player player, CommandContext args, Resident resident, @Nullable Town town, @Nullable Nation nation) {
        if ( town == null ) return CommandResult.empty();

        Optional<Nation> n = NationManager.getInstance().getByName( args.<String>getOne("nation").orElse(UUID.randomUUID().toString()) );

        if ( n.isPresent() ) {
            town.setParent(n.get());
            TownMessage.informAll(Text.of(town.getName() + " has joined the nation of " + n.get().getName() ));
        } else {
            TownMessage.warn(player, "You must provide a valid nation to join.");
        }

        return CommandResult.empty();
    }

    @Override
    public CommandSpec getSpec() {
        return CommandSpec.builder()
                .description( Text.of( "Used to change the nation of the town." ) )
                .permission( TownActions.SET_NATION.getPermission() )
                .arguments(
                        GenericArguments.remainingJoinedStrings(Text.of("nation"))
                )
                .executor( new TownSetNationCommand() )
                .build();
    }
}
