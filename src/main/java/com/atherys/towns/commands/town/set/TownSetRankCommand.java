package com.atherys.towns.commands.town.set;

import com.atherys.towns.managers.ResidentManager;
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
import org.spongepowered.api.text.Text;

import javax.annotation.Nullable;
import java.util.Optional;

public class TownSetRankCommand extends AbstractTownSetCommand {

    TownSetRankCommand() {
        super(
                new String[] { "rank" },
                "rank <player> <newRank>",
                Text.of("Used to change the rank of a town resident."),
                TownRank.Action.SET_RANK
        );
    }

    @Override
    public CommandResult townsExecute(@Nullable Nation nation, @Nullable Town town, Resident resident, Player player, CommandContext args) {

        if ( town == null ) return CommandResult.empty();

        Optional<Player> target = args.getOne("player");
        if ( !target.isPresent() ) {
            TownMessage.warn( player, "You must provide a valid player.");
            return CommandResult.empty();
        }

        Optional<Resident> targetResOpt = ResidentManager.getInstance().get( target.get().getUniqueId() );
        if ( !targetResOpt.isPresent() ) {
            TownMessage.warn( player, "You must provide a valid resident.");
            return CommandResult.empty();
        }

        Resident targetRes = targetResOpt.get();
        if ( !targetRes.getTown().isPresent() || !town.equals( targetRes.getTown().get() ) ) {
            TownMessage.warn( player, "The specified resident must be part of your town.");
            return CommandResult.empty();
        }

        TownRank rank = (TownRank) args.getOne("newRank").orElse(targetRes.getTownRank());
        if ( rank.equals(TownRank.MAYOR) ) {
            TownMessage.warn( player, "You cannot set the town mayor using this command. Please use '/t set mayor'");
            return CommandResult.empty();
        } else if ( rank.equals(TownRank.NONE) ) {
            rank = TownRank.RESIDENT;
        }

        targetRes.setTownRank( rank );
        town.informResidents(Text.of( targetRes.getName() + " has been given the rank of " + rank.formattedName() ));

        return CommandResult.success();
    }

    @Override
    public CommandSpec getSpec() {
        return CommandSpec.builder()
                .permission("atherys.towns.commands.town.set.rank")
                .description(Text.of("Used to set the rank of another resident."))
                .arguments(
                        GenericArguments.player(Text.of("player")),
                        GenericArguments.enumValue(Text.of("newRank"), TownRank.class)
                )
                .executor(this)
                .build();
    }
}
