package com.atherys.towns.commands.town.set;

import com.atherys.towns.AtherysTowns;
import com.atherys.towns.messaging.TownMessage;
import com.atherys.towns.nation.Nation;
import com.atherys.towns.resident.Resident;
import com.atherys.towns.resident.ranks.TownRank;
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

public class TownSetMayorCommand extends AbstractTownSetCommand {

    TownSetMayorCommand() {
        super(
                new String[] { "mayor" },
                "mayor <player>",
                Text.of("Used to change the mayor of a town."),
                TownRank.Action.SET_MAYOR
        );
    }

    @Override
    public CommandResult townsExecute(@Nullable Nation nation, @Nullable Town town, Resident resident, Player player, CommandContext args) {
        if ( town == null ) return CommandResult.empty();

        Optional<User> newMayor = args.getOne("newMayor");

        if ( newMayor.isPresent() ) {
            Optional<Resident> newMayorRes = AtherysTowns.getInstance().getResidentManager().get( newMayor.get().getUniqueId() );
            if (newMayorRes.isPresent()) {
                if ( !newMayorRes.get().town().isPresent() || ( newMayorRes.get().town().isPresent() && !newMayorRes.get().town().get().equals(town) ) ) {
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
                .permission("atherys.towns.commands.town.set.mayor")
                .description(Text.of("Used to set a new mayor for the town."))
                .arguments(GenericArguments.user(Text.of("newMayor")))
                .executor(this)
                .build();
    }
}
