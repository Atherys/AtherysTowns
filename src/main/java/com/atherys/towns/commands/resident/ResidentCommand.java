package com.atherys.towns.commands.resident;

import com.atherys.core.command.ParameterizedCommand;
import com.atherys.core.command.annotation.Aliases;
import com.atherys.core.command.annotation.Description;
import com.atherys.towns.managers.ResidentManager;
import com.atherys.towns.messaging.TownMessage;
import com.atherys.towns.resident.Resident;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;

import java.util.Optional;

@Aliases({"res", "resident"})
@Description("Used to get information on a resident.")
public class ResidentCommand implements ParameterizedCommand {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (!(src instanceof Player)) {
            return CommandResult.empty();
        }

        if (args.hasAny("player")) {
            // get other player resident
            Optional<User> user = args.getOne(Text.of("player"));
            if (user.isPresent()) {
                System.out.println(user.get().getUniqueId().toString());
                Optional<Resident> res = ResidentManager.getInstance()
                        .get(user.get().getUniqueId());
                if (res.isPresent()) {
                    res.get().createView().show((Player) src);
                } else {
                    TownMessage.warn((Player) src, "Resident does not exist.");
                }
                return CommandResult.success();
            }
        } else {
            // get own resident
            Optional<Resident> res = ResidentManager.getInstance()
                    .get(((Player) src).getUniqueId());
            // send src resident info
            res.ifPresent(resident -> resident.createView().show((Player) src));
        }

        return CommandResult.empty();
    }

    @Override
    public CommandElement[] getArguments() {
        return new CommandElement[]{
                GenericArguments.optional(GenericArguments.user(Text.of("player")))
        };
    }
}
