package com.atherys.towns.commands.nation;

import com.atherys.towns.messaging.TownMessage;
import com.atherys.towns.nation.Nation;
import com.atherys.towns.resident.Resident;
import com.atherys.towns.resident.ranks.NationRank;
import com.atherys.towns.town.Town;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import javax.annotation.Nullable;

public class NationCreateCommand extends AbstractNationCommand {

    protected NationCreateCommand() {
        super(
                new String[] { "create", "new" },
                "create <nationName>",
                Text.of("Used to create a new nation with your town as it's capital."),
                NationRank.Action.CREATE_NATION,
                true, // check town
                false, // check nation
                true, // check action
                true
        );
    }

    @Override
    public CommandResult townsExecute(@Nullable Nation nation, @Nullable Town town, Resident resident, Player player, CommandContext args) {
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
                .permission("atherys.towns.commands.nation.create")
                .description(Text.of("Used to create a new nation."))
                .arguments(GenericArguments.remainingJoinedStrings(Text.of("nationName")))
                .executor(this)
                .build();
    }
}
