package com.atherys.towns.commands.town.set;

import com.atherys.core.command.ParameterizedCommand;
import com.atherys.core.command.annotation.Aliases;
import com.atherys.core.command.annotation.Description;
import com.atherys.core.command.annotation.Permission;
import com.atherys.towns.commands.TownsCommand;
import com.atherys.towns.managers.ResidentManager;
import com.atherys.towns.messaging.TownMessage;
import com.atherys.towns.nation.Nation;
import com.atherys.towns.resident.Resident;
import com.atherys.towns.town.Town;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;

import javax.annotation.Nullable;
import java.util.Optional;

@Aliases({"mayor", "leader"})
@Description("Used to change the leader of a town.")
@Permission("atherystowns.town.set.mayor")
public class TownSetMayorCommand extends TownsCommand implements ParameterizedCommand {

    @Override
    protected CommandResult execute(Player player, CommandContext args, Resident resident,
                                    @Nullable Town town, @Nullable Nation nation) {
        if (town == null) {
            return CommandResult.empty();
        }

        Optional<User> newMayor = args.getOne("newMayor");

        if (newMayor.isPresent()) {
            Optional<Resident> newMayorRes = ResidentManager.getInstance()
                    .get(newMayor.get().getUniqueId());
            if (newMayorRes.isPresent()) {
                if (!newMayorRes.get().getTown().isPresent() || (
                        newMayorRes.get().getTown().isPresent() && !newMayorRes.get().getTown().get()
                                .equals(town))) {
                    TownMessage.warn(player,
                            "The player specified must be part of your town in order to replace the current mayor.");
                    return CommandResult.empty();
                }
                town.setMayor(newMayorRes.get());
                town.informResidents(
                        Text.of(newMayorRes.get().getName(), " has been set as the new town Mayor!"));
                return CommandResult.success();
            } else {
                TownMessage.warn(player, Text.of("You must provide a valid resident."));
                return CommandResult.empty();
            }
        }

        return CommandResult.empty();
    }

    @Override
    public CommandElement[] getArguments() {
        return new CommandElement[]{
                GenericArguments.user(Text.of("newMayor"))
        };
    }
}
