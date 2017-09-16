package com.atherys.towns.commands.town;

import com.atherys.towns.Settings;
import com.atherys.towns.commands.AbstractCommand;
import com.atherys.towns.nation.Nation;
import com.atherys.towns.resident.Resident;
import com.atherys.towns.resident.ranks.TownRank;
import com.atherys.towns.town.Town;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextStyles;

import javax.annotation.Nullable;

public class TownHelpCommand extends AbstractTownCommand {

    TownHelpCommand () {
        super(
                new String[] { "help" },
                "help",
                Text.of("Used to get help for the Master Towns Command."),
                TownRank.Action.NONE,
                false,
                false,
                false,
                true
        );
    }

    @Override
    public CommandResult townsExecute(@Nullable Nation nation, @Nullable Town town, Resident resident, Player player, CommandContext args) {
        player.sendMessage(Text.of(Settings.DECORATION_COLOR, ".o0o.=---------= { ", TextStyles.BOLD, Settings.PRIMARY_COLOR, "/t(own) Help", TextStyles.RESET, Settings.DECORATION_COLOR, " } =---------=.o0o." ));
        for ( AbstractCommand cmd : TownMasterCommand.getInstance().getChildren() ) {
            cmd.sendInfo(player);
        }
        player.sendMessage(Text.of(Settings.DECORATION_COLOR, ".o0o.=---------= { ", TextStyles.BOLD, Settings.PRIMARY_COLOR, "/t(own) Help", TextStyles.RESET, Settings.DECORATION_COLOR, " } =---------=.o0o." ));
        return CommandResult.empty();
    }

    @Override
    public CommandSpec getSpec() {
        return CommandSpec.builder()
                .permission("atherys.commands.towns.help")
                .description(Text.of("Get help for the Towns Master Command(s)."))
                .executor(this)
                .build();
    }
}
