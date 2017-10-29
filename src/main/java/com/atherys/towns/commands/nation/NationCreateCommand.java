package com.atherys.towns.commands.nation;

import com.atherys.towns.commands.TownsSimpleCommand;
import com.atherys.towns.messaging.TownMessage;
import com.atherys.towns.nation.Nation;
import com.atherys.towns.permissions.actions.TownAction;
import com.atherys.towns.resident.Resident;
import com.atherys.towns.town.Town;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import javax.annotation.Nullable;

public class NationCreateCommand extends TownsSimpleCommand {

    private static NationCreateCommand instance = new NationCreateCommand();

    public static NationCreateCommand getInstance() {
        return instance;
    }

    @Override
    protected CommandResult execute(Player player, CommandContext args, Resident resident, @Nullable Town town, @Nullable Nation nation) {
        if ( nation != null ) {
            TownMessage.warn(player, "You must leave your current nation and be the mayor of an independent town in order to create a new nation.");
            return CommandResult.empty();
        }

        Nation newNation = Nation.create( args.<String>getOne("nationName").orElse( town + "'s Nation" ), town );
        player.sendMessage(newNation.getFormattedInfo());

        return CommandResult.success();
    }

    @Override
    public CommandSpec getSpec() {
        return CommandSpec.builder()
                .permission(TownAction.CREATE_NATION.getPermission())
                .description(Text.of("Used to create a new nation."))
                .arguments(GenericArguments.remainingJoinedStrings(Text.of("nationName")))
                .executor(this)
                .build();
    }
}
