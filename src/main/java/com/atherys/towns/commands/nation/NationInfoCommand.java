package com.atherys.towns.commands.nation;

import com.atherys.towns.managers.NationManager;
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

import java.util.Optional;

public class NationInfoCommand extends AbstractNationCommand {

    protected NationInfoCommand() {
        super(
                new String[]{ "info" },
                "info [nation]",
                Text.of("Get information on a nation."),
                NationRank.Action.NONE,
                false,
                false,
                false,
                true
        );
    }

    @Override
    public CommandSpec getSpec() {
        return CommandSpec.builder()
                .permission("atherys.towns.commands.nation.info")
                .description(Text.of("Used to get info on a nation."))
                .arguments(GenericArguments.optional(GenericArguments.remainingJoinedStrings(Text.of("nation"))))
                .executor(this)
                .build();
    }

    @Override
    public CommandResult townsExecute(Nation nation, Town town, Resident resident, Player player, CommandContext args) {

        Optional<Nation> nationOptional = NationManager.getInstance().getByName(args.<String>getOne("nation").orElseGet( () -> {
            Optional<Nation> residentNation = resident.getNation();
            if ( residentNation.isPresent() ) return residentNation.get().getName();
            return "";
        }));
        nationOptional.ifPresent(nation1 -> player.sendMessage(nation1.getFormattedInfo()));
        return CommandResult.success();
    }
}
