package com.atherys.towns.commands.town;

import com.atherys.towns.commands.town.set.AbstractTownSetCommand;
import com.atherys.towns.nation.Nation;
import com.atherys.towns.resident.Resident;
import com.atherys.towns.resident.ranks.TownRank;
import com.atherys.towns.town.Town;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import javax.annotation.Nullable;

public class TownMasterCommand extends AbstractTownCommand {

    private static final TownMasterCommand instance = new TownMasterCommand();

    private TownMasterCommand() {
        super(
                new String[] { "t", "town" },
                "[subcommand]",
                Text.of( "The master /town command." ),
                TownRank.Action.NONE,
                false,
                false,
                false,
                false
        );
    }

    @Override
    public CommandResult townsExecute(@Nullable Nation nation, @Nullable Town town, Resident resident, Player player, CommandContext args) {
        if ( resident.town().isPresent() ) {
            player.sendMessage(resident.town().get().getFormattedInfo());
        }
        return CommandResult.empty();
    }

    @Override
    public CommandSpec getSpec() {
        return null;
    }

    public void register() {

        new TownHelpCommand();
        new TownCreateCommand();
        new TownClaimCommand();
        new TownUnclaimCommand();
        new TownInfoCommand();
        new TownHereCommand();
        new TownInviteCommand();
        new TownJoinCommand();
        new TownLeaveCommand();
        new TownRuinCommand();
        new TownDepositCommand();
        new TownWithdrawCommand();
        new TownSpawnCommand();
        new TownBorderCommand();
        new AbstractTownSetCommand();

        register(
                CommandSpec.builder()
                .permission("atherys.towns.commands.town")
                .description(Text.of("Master Town command."))
                .executor(this)
        );
    }

    public static TownMasterCommand getInstance() {
        return instance;
    }
}
