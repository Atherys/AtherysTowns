package com.atherys.towns.commands.town;

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

public class TownInviteCommand extends AbstractTownCommand {

    TownInviteCommand() {
        super(
                new String[] { "invite", "add" },
                "invite [player]",
                Text.of("Used to invite a player to your town."),
                TownRank.Action.INVITE_PLAYER,
                true,
                false,
                true,
                true
        );
    }

    @Override
    public CommandResult townsExecute(@Nullable Nation nation, @Nullable Town town, Resident resident, Player player, CommandContext args) {

        if ( town == null ) {
            return CommandResult.empty();
        }

        Optional<Player> invitee = args.getOne("player");
        if ( !invitee.isPresent() ) {
            TownMessage.warn( player, "You must provide a valid ( online ) player whom to invite to your town." );
            return CommandResult.empty();
        }

        Player p = invitee.get();
        Optional<Resident> otherRes = ResidentManager.getInstance().get( p.getUniqueId() );

        if ( otherRes.isPresent() ) {

            if ( !resident.equals( otherRes.get() ) ) {
                town.informResidents(Text.of( player.getName(), " has invited ", p.getName(), " to the town."));
                town.inviteResident(otherRes.get());
                return CommandResult.success();
            } else {
                TownMessage.warn(player, "You can't invite yourself!");
            }
        }

        return CommandResult.empty();
    }

    @Override
    public CommandSpec getSpec() {
        return  CommandSpec.builder()
                .permission("atherys.towns.commands.town.invite")
                .description(Text.of("Used to invite a resident to the town."))
                .arguments(GenericArguments.onlyOne(GenericArguments.player(Text.of("player"))))
                .executor(this)
                .build();
    }
}
