package com.atherys.towns.commands.town;

import com.atherys.towns.Settings;
import com.atherys.towns.commands.TownsSimpleCommand;
import com.atherys.towns.nation.Nation;
import com.atherys.towns.resident.Resident;
import com.atherys.towns.town.Town;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextStyles;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public class TownHelpCommand extends TownsSimpleCommand {

    private static TownHelpCommand instance = new TownHelpCommand();

    public static TownHelpCommand getInstance() {
        return instance;
    }

    @Override
    protected CommandResult execute(Player player, CommandContext args, Resident resident, @Nullable Town town, @Nullable Nation nation) {
        player.sendMessage(Text.of(Settings.DECORATION_COLOR, ".o0o.=---------= { ", TextStyles.BOLD, Settings.PRIMARY_COLOR, "/t(own) Help", TextStyles.RESET, Settings.DECORATION_COLOR, " } =---------=.o0o." ));

        for (Map.Entry<List<String>, CommandCallable> entry : TownMasterCommand.getInstance().getChildren().entrySet() ) {
            Text helpMsg = Text.builder()
                    .append( Text.of( TextStyles.BOLD, Settings.PRIMARY_COLOR, entry.getValue().getUsage(player) ) )
                    .onHover(TextActions.showText(
                            entry.getValue().getHelp( player ).orElse( Text.of("Help Unavailable") )
                    ))
                    .onClick(TextActions.suggestCommand( entry.getValue().getUsage( player ).toPlain() ) )
                    .build();
            player.sendMessage( helpMsg );
        }

        player.sendMessage(Text.of(Settings.DECORATION_COLOR, ".o0o.=---------= { ", TextStyles.BOLD, Settings.PRIMARY_COLOR, "/t(own) Help", TextStyles.RESET, Settings.DECORATION_COLOR, " } =---------=.o0o." ));
        return CommandResult.empty();
    }

    @Override
    public CommandSpec getSpec() {
        return CommandSpec.builder()
                .description( Text.of ( "Used to get help for the Master Towns Command." ) )
                .executor( this )
                .build();
    }
}
