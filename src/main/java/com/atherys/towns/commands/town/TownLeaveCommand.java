package com.atherys.towns.commands.town;

import com.atherys.core.command.annotation.Aliases;
import com.atherys.core.command.annotation.Description;
import com.atherys.core.command.annotation.Permission;
import com.atherys.core.utils.Question;
import com.atherys.towns.commands.TownsCommand;
import com.atherys.towns.nation.Nation;
import com.atherys.towns.permissions.ranks.TownRanks;
import com.atherys.towns.resident.Resident;
import com.atherys.towns.town.Town;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import javax.annotation.Nullable;
import java.util.Optional;

@Aliases("leave")
@Description("Used to leave a town. If you are not part of a town, this command will not function.")
@Permission("atherystowns.town.leave")
public class TownLeaveCommand extends TownsCommand {

    private static TownLeaveCommand instance = new TownLeaveCommand();

    public static TownLeaveCommand getInstance() {
        return instance;
    }

    public static void leaveTown(Resident resident) {
        Optional<Player> player = resident.getPlayer();
        if (!player.isPresent()) {
            return;
        }

        Question leave = Question.of(Text.of("Would you like to leave your current town?"))
                .addAnswer(Question.Answer.of(Text.of(TextColors.GREEN, "Yes"), player1 -> {
                    if (resident.getTown().isPresent()) {
                        resident.setTown(null, TownRanks.NONE);
                        resident.getTown().get()
                                .warnResidents(Text.of(player.get().getName() + " has left the town."));
                    }
                }))
                .addAnswer(Question.Answer.of(Text.of(TextColors.RED, "No"), player1 -> {
                }))
                .build();

        leave.pollChat(player.get());
    }

    @Override
    protected CommandResult execute(Player player, CommandContext args, Resident resident,
                                    @Nullable Town town, @Nullable Nation nation) {

        leaveTown(resident);

        return CommandResult.success();
    }
}
