package com.atherys.towns.command;

import com.atherys.core.command.ParameterizedCommand;
import com.atherys.core.command.annotation.Aliases;
import com.atherys.towns.db.ResidentManager;
import com.atherys.towns.model.Resident;
import com.atherys.towns.service.TownService;
import com.google.inject.Inject;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

@Aliases("towncreate")
public class TownCreateCommand implements ParameterizedCommand {

    private TownService townService;
    private ResidentManager residentManager;

    @Inject
    public TownCreateCommand(TownService townService, ResidentManager residentManager) {
        this.townService = townService;
        this.residentManager = residentManager;
    }

    @Override
    public CommandElement[] getArguments() {
        return new CommandElement[] {
                GenericArguments.vector3d(Text.of("corner1")),
                GenericArguments.vector3d(Text.of("corner2")),
                GenericArguments.text(Text.of("name"), TextSerializers.FORMATTING_CODE, true)
        };
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if ( src instanceof Player ) {

            Resident resident = residentManager.getOrCreate((Player) src);



            return CommandResult.success();
        } else return CommandResult.empty();
    }
}
