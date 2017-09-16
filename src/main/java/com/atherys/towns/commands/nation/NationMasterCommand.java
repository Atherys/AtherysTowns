package com.atherys.towns.commands.nation;

import com.atherys.towns.Settings;
import com.atherys.towns.commands.AbstractCommand;
import com.atherys.towns.nation.Nation;
import com.atherys.towns.resident.Resident;
import com.atherys.towns.resident.ranks.NationRank;
import com.atherys.towns.town.Town;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextStyles;

import javax.annotation.Nullable;

public class NationMasterCommand extends AbstractNationCommand {

    private static final NationMasterCommand instance = new NationMasterCommand();

    private NationMasterCommand() {
        super(
                new String[] { "n", "nation" },
                "",
                Text.of("Master Nation Command."),
                NationRank.Action.NONE,
                false,
                false,
                false,
                false
        );
    }

    @Override
    public CommandResult townsExecute(@Nullable Nation nation, @Nullable Town town, Resident resident, Player player, CommandContext args) {
        if ( resident.town().isPresent() && resident.town().get().getParent().isPresent() ) {
            player.sendMessage(resident.town().get().getParent().get().formatInfo());
        } else {
            player.sendMessage(Text.of(Settings.DECORATION_COLOR, ".o0o.=---------= { ", TextStyles.BOLD, Settings.PRIMARY_COLOR, "/n(ation) Help", TextStyles.RESET, Settings.DECORATION_COLOR, " } =---------=.o0o." ));
            for ( AbstractCommand cmd : getChildren() ) {
                cmd.sendInfo(player);
            }
            player.sendMessage(Text.of(Settings.DECORATION_COLOR, ".o0o.=---------= { ", TextStyles.BOLD, Settings.PRIMARY_COLOR, "/n(ation) Help", TextStyles.RESET, Settings.DECORATION_COLOR, " } =---------=.o0o." ));
        }
        return CommandResult.empty();
    }

    @Override
    public CommandSpec getSpec() {
        return CommandSpec.builder()
                .permission("atherys.towns.commands.nation")
                .description(Text.of("Master Nation command."))
                .executor(this)
                .build();
    }

    /*@Override
    public final <T extends AbstractCommand> void register( Collection<T> children ) {

        CommandSpec.Builder spec = CommandSpec.builder()
                .permission("atherys.towns.commands.nation")
                .description(Text.of("Master Nation command."))
                .executor(this);

        for ( AbstractCommand ncmd : children ) {
            if ( ncmd.getAliases() == null ) {
                AtherysTowns.getInstance().getLogger().severe("Command " + ncmd.getClass().getName() + " does not have any aliases. Will not register.");
                continue;
            }
            if ( ncmd.getSpec() == null ) {
                AtherysTowns.getInstance().getLogger().severe("Command " + ncmd.getClass().getName() + " does not have a spec. Will not register.");
                continue;
            }
            ncmd.register(null);
            spec.child( ncmd.getSpec(), ncmd.getAliases() );
        }

        Sponge.getCommandManager().register(AtherysTowns.getInstance(), spec.build(), "n", "nation");
    }*/

    public void register() {

        new NationCreateCommand();
        new NationInfoCommand();
        new NationDepositCommand();
        new NationWithdrawCommand();

        this.register(
                CommandSpec.builder()
                .permission("atherys.towns.commands.nation")
                .description(Text.of("Master Nation command."))
                .executor(this)
        );
    }

    public static NationMasterCommand getInstance() {
        return instance;
    }
}
