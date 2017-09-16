package com.atherys.towns.commands.plot;

import com.atherys.towns.Settings;
import com.atherys.towns.commands.AbstractCommand;
import com.atherys.towns.nation.Nation;
import com.atherys.towns.resident.Resident;
import com.atherys.towns.town.Town;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextStyles;

import javax.annotation.Nullable;

public class PlotMasterCommand extends AbstractPlotCommand {

    private static final PlotMasterCommand instance = new PlotMasterCommand();

    private PlotMasterCommand() {
        super(
                new String[] { "p", "plot" },
                "",
                Text.of("Master Plot Command."),
                false
        );
    }

    @Override
    public CommandResult townsExecute(@Nullable Nation nation, @Nullable Town town, Resident resident, Player player, CommandContext args) {
        player.sendMessage(Text.of(Settings.DECORATION_COLOR, ".o0o.=---------= { ", TextStyles.BOLD, Settings.PRIMARY_COLOR, "/p(lot) Help", TextStyles.RESET, Settings.DECORATION_COLOR, " } =---------=.o0o." ));
        for ( AbstractCommand cmd : getChildren() ) {
            cmd.sendInfo(player);
        }
        player.sendMessage(Text.of(Settings.DECORATION_COLOR, ".o0o.=---------= { ", TextStyles.BOLD, Settings.PRIMARY_COLOR, "/p(lot) Help", TextStyles.RESET, Settings.DECORATION_COLOR, " } =---------=.o0o." ));
        return CommandResult.empty();
    }

    @Override
    public CommandSpec getSpec() {
        return CommandSpec.builder()
                .permission("atherys.towns.commands.plot")
                .description(Text.of("Information on the /plot command"))
                .executor(this)
                .build();
    }

    /*@Override
    public <T extends AbstractCommand> void register(Collection<T> children) {
        CommandSpec.Builder spec = CommandSpec.builder()
                .permission("atherys.towns.commands.plot")
                .description(Text.of("Master Plot command."))
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

        Sponge.getCommandManager().register(AtherysTowns.getInstance(), spec.build(), "p", "plot");
    }*/

    public void register() {

        new PlotToolCommand();
        new PlotHereCommand();
        new PlotDeselectCommand();
        new PlotSetFlagCommand();
        new PlotSetNameCommand();

        this.register(
                CommandSpec.builder()
                .permission("atherys.towns.commands.plot")
                .description(Text.of("Master Plot command."))
                .executor(this)
        );
    }

    public static PlotMasterCommand getInstance() {
        return instance;
    }
}
