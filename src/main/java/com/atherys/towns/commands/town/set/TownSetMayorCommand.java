package com.atherys.towns.commands.town.set;

import com.atherys.towns.commands.TownsSimpleCommand;
import com.atherys.towns.managers.ResidentManager;
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
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;

import javax.annotation.Nullable;
import java.util.Optional;

public class TownSetMayorCommand extends TownsSimpleCommand {

    private static TownSetMayorCommand instance = new TownSetMayorCommand();

    public static TownSetMayorCommand getInstance() {
        return instance;
    }

    @Override
    protected CommandResult execute(Player player, CommandContext args, Resident resident, @Nullable Town town, @Nullable Nation nation) {
        if ( town == null ) return CommandResult.empty();

        Optional<User> newMayor = args.getOne("newMayor");

        if ( newMayor.isPresent() ) {
            Optional<Resident> newMayorRes = ResidentManager.getInstance().get( newMayor.get().getUniqueId() );
            if (newMayorRes.isPresent()) {
                if ( !newMayorRes.get().getTown().isPresent() || ( newMayorRes.get().getTown().isPresent() && !newMayorRes.get().getTown().get().equals(town) ) ) {
                    TownMessage.warn( player, "The player specified must be part of your town in order to replace the current mayor.");
                    return CommandResult.empty();
                }
                town.setMayor(newMayorRes.get());
                town.informResidents(Text.of( newMayorRes.get().getName(), " has been set as the new town Mayor!"));
                return CommandResult.success();
            } else {
                TownMessage.warn( player, Text.of("You must provide a valid resident."));
                return CommandResult.empty();
            }
        }

        return CommandResult.empty();
    }

    @Override
    public CommandSpec getSpec() {
        return CommandSpec.builder()
                .description( Text.of( "Used to change the mayor of the town." ) )
                .permission( TownActions.SET_MAYOR.getPermission() )
                .arguments(
                        GenericArguments.user(Text.of("newMayor"))
                )
                .executor( new TownSetMayorCommand() )
                .build();
    }
}
