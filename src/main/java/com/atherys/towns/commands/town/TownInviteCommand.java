package com.atherys.towns.commands.town;

import com.atherys.core.utils.Question;
import com.atherys.towns.AtherysTowns;
import com.atherys.towns.commands.TownsSimpleCommand;
import com.atherys.towns.managers.ResidentManager;
import com.atherys.towns.messaging.TownMessage;
import com.atherys.towns.nation.Nation;
import com.atherys.towns.permissions.actions.TownActions;
import com.atherys.towns.permissions.ranks.TownRanks;
import com.atherys.towns.resident.Resident;
import com.atherys.towns.town.Town;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import javax.annotation.Nullable;
import java.util.Optional;

public class TownInviteCommand extends TownsSimpleCommand {

    private static TownInviteCommand instance = new TownInviteCommand();

    public static TownInviteCommand getInstance() {
        return instance;
    }

    @Override
    protected CommandResult execute(Player player, CommandContext args, Resident resident, @Nullable Town town, @Nullable Nation nation) {
        if ( town == null ) {
            return CommandResult.empty();
        }

        Optional<Player> invitedPlayer = args.getOne("player");
        if ( !invitedPlayer.isPresent() ) {
            TownMessage.warn( player, "You must provide a valid ( online ) player whom to invite to your town." );
            return CommandResult.empty();
        }

        Player p = invitedPlayer.get();
        Optional<Resident> otherRes = ResidentManager.getInstance().get( p.getUniqueId() );

        if ( otherRes.isPresent() ) {

            if ( !resident.equals( otherRes.get() ) ) {
                town.informResidents(Text.of( player.getName(), " has invited ", p.getName(), " to the town."));
                inviteResident( otherRes.get(), town );
                return CommandResult.success();
            } else {
                TownMessage.warn(player, "You can't invite yourself!");
            }
        }

        return CommandResult.empty();
    }

    @Override
    public CommandSpec getSpec() {
        return CommandSpec.builder()
                .description( Text.of( "Used to invite somebody to the town." ) )
                .permission( TownActions.INVITE_PLAYER.getPermission() )
                .arguments(
                        GenericArguments.onlyOne(GenericArguments.player(Text.of("player")))
                )
                .executor( this )
                .build();
    }

    public void inviteResident(Resident resident, Town town) {
        Optional<Player> p = resident.getPlayer();

        if ( p.isPresent() ) {
            Text question;
            if ( town.getParent().isPresent() ) {
                question = Text.of("You have been invited to the town of ", town.getName(), " in ", town.getParent().get().getName() );
            } else {
                question = Text.of("You have been invited to the town of ", town.getName() );
            }

            Question invite = Question.of( question )
                    .addAnswer( Question.Answer.of( Text.of( TextColors.GREEN, "Accept" ), player -> {
                        if ( resident.getTown().isPresent() ) {
                            TownMessage.warn( player, Text.of("You cannot join a town while you are part of another! Please leave your current town first.") );
                            return;
                        }
                        resident.setTown( town, TownRanks.RESIDENT);
                        town.informResidents( Text.of( player.getName(), " has joined the town."));
                    }))
                    .addAnswer( Question.Answer.of( Text.of( TextColors.RED, "Reject" ), player -> {
                        town.informResidents( Text.of( player.getName(), " has rejected the invitation to the town."));
                    }))
                    .build();

            invite.pollViewButton( p.get(), Text.of( TownMessage.MSG_PREFIX, AtherysTowns.getConfig().COLORS.TERTIARY, "You have been invited to join the town of ", town.getName(), ". Click here to respond." ) );
        }
    }
}
