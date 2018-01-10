package com.atherys.towns.commands.town;

import com.atherys.towns.commands.TownsSimpleCommand;
import com.atherys.towns.managers.TownManager;
import com.atherys.towns.messaging.TownMessage;
import com.atherys.towns.nation.Nation;
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

public class TownInfoCommand extends TownsSimpleCommand {

    private static TownInfoCommand instance = new TownInfoCommand();

    public static TownInfoCommand getInstance() {
        return instance;
    }

    @Override
    protected CommandResult execute(Player player, CommandContext args, Resident resident, @Nullable Town town, @Nullable Nation nation) {
        Optional<String> townName = args.getOne("townName");

        if ( !townName.isPresent() ) {

            if (resident.getTown().isPresent()) {
                Town t = resident.getTown().get();
                t.createView().ifPresent( view -> view.show(player) );
                return CommandResult.success();
            } else {
                TownMessage.warn(player, Text.of("You are not part of a town!"));
                return CommandResult.empty();
            }

        } else {

            Optional<Town> tOpt;
            Text error;

            if ( townName.get().equalsIgnoreCase("here") ) {
                error = Text.of("You are in the wilderness.");
                tOpt = TownManager.getInstance().getByLocation(player.getLocation());
            } else {
                error = Text.of("No such town exists.");
                tOpt = TownManager.getInstance().getByName(townName.get());
            }

            if ( tOpt.isPresent() ) {
                tOpt.get().createView().ifPresent( view -> view.show(player) );
            } else {
                TownMessage.warn(player, error);
                return CommandResult.empty();
            }
        }

        return CommandResult.empty();
    }

    @Override
    public CommandSpec getSpec() {
        return CommandSpec.builder()
                .description( Text.of( "Used to get information on a town based on it's name." ) )
                .arguments(
                        GenericArguments.optional(GenericArguments.remainingJoinedStrings(Text.of("townName")))
                )
                .executor( this )
                .build();
    }
}
