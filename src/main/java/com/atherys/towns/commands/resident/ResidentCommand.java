package com.atherys.towns.commands.resident;

import com.atherys.towns.AtherysTowns;
import com.atherys.towns.managers.ResidentManager;
import com.atherys.towns.messaging.TownMessage;
import com.atherys.towns.resident.Resident;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;

import java.util.Optional;

public class ResidentCommand implements CommandExecutor {

    public static void register() {
        CommandSpec spec = CommandSpec.builder()
                .executor(new ResidentCommand())
                .description(Text.of("Used to get information on a resident."))
                .arguments(GenericArguments.optional(GenericArguments.user(Text.of("player"))))
                .build();


        AtherysTowns.getInstance().getGame().getCommandManager().register(AtherysTowns.getInstance(), spec, "res", "resident");
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
            if ( !(src instanceof Player) ) return CommandResult.empty();

            if ( args.hasAny("player") ) {
                // get other player resident
                Optional<User> user = args.getOne(Text.of("player"));
                if ( user.isPresent() ) {
                    System.out.println(user.get().getUniqueId().toString());
                    Optional<Resident> res = ResidentManager.getInstance().get(user.get().getUniqueId());
                    if ( res.isPresent() ) {
                        res.get().createView().ifPresent( view -> view.show( (Player) src ) );
                    } else {
                        TownMessage.warn((Player) src, "Resident does not exist.");
                    }
                    return CommandResult.success();
                }
            } else {
                // get own resident
                Optional<Resident> res = ResidentManager.getInstance().get(((Player) src).getUniqueId());
                // send src resident info
                res.ifPresent(resident -> resident.createView().ifPresent( view -> view.show( (Player) src ) ) );
            }

        return CommandResult.empty();
    }
}
