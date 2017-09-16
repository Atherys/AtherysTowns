package com.atherys.towns.commands;

import com.atherys.towns.AtherysTowns;
import com.atherys.towns.Settings;
import com.atherys.towns.messaging.TownMessage;
import com.atherys.towns.nation.Nation;
import com.atherys.towns.resident.Resident;
import com.atherys.towns.resident.ranks.NationRank;
import com.atherys.towns.resident.ranks.TownsAction;
import com.atherys.towns.town.Town;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextStyles;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public abstract class AbstractCommand implements CommandExecutor, AbstractCommandExecutor {

    private String[] aliases;

    private Text syntax;
    private Text description;

    boolean checkTown;
    boolean checkNation;
    boolean checkAction;

    TownsAction action;

    List<AbstractCommand> children = new LinkedList<>();

    protected AbstractCommand ( String[] aliases, Text syntax, Text description, TownsAction action, boolean checkTown, boolean checkNation, boolean checkAction ) {
        this.syntax = syntax;
        this.description = description;
        this.aliases = aliases;
        this.checkAction = checkAction;
        this.checkNation = checkNation;
        this.checkTown = checkTown;
        this.action = action;
    }

    public void sendInfo ( Player player ) {
        player.sendMessage(
                Text.builder()
                .append(
                        Text.of(TextStyles.BOLD, syntax)
                                .toBuilder()
                                .onHover(TextActions.showText(Text.of(Settings.TERTIARY_COLOR, description)))
                                .onClick(TextActions.suggestCommand(syntax.toPlain()))
                                .build())
                .build()
        );
    }

    public String[] getAliases() { return aliases; }

    public boolean isTownRelevant() { return checkTown; }

    public boolean isNationRelevant() { return checkNation; }

    public boolean isActionRelevant() { return checkAction; }

    public abstract CommandSpec getSpec();

    protected void register( CommandSpec.Builder spec ) {

        for ( AbstractCommand ncmd : children ) {
            if ( ncmd.getAliases() == null ) {
                AtherysTowns.getInstance().getLogger().error("Command " + ncmd.getClass().getName() + " does not have any aliases. Will not register.");
                continue;
            }
            if ( ncmd.getSpec() == null ) {
                AtherysTowns.getInstance().getLogger().error("Command " + ncmd.getClass().getName() + " does not have a spec. Will not register.");
                continue;
            }
            spec.child( ncmd.getSpec(), ncmd.getAliases() );
        }

        Sponge.getCommandManager().register(AtherysTowns.getInstance(), spec.build(), aliases);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if ( !(src instanceof Player) ) {
            src.sendMessage(Text.of("Must be player to execute this command."));
            return CommandResult.empty();
        }

        Player player = (Player) src;
        Resident res;
        Town t = null;
        Nation n = null;

        Optional<Resident> resOpt = AtherysTowns.getInstance().getResidentManager().get(player.getUniqueId());
        if ( !resOpt.isPresent()) {
            return CommandResult.empty();
        } else res = resOpt.get(); // resident confirmed

        if ( isTownRelevant() ) {
            if (!res.town().isPresent()) {
                TownMessage.warn(player, "You must be part of a town to do this command.");
                return CommandResult.empty();
            } else t = res.town().get(); // town confirmed
        }

        if ( isNationRelevant() ) {
            if ( !res.town().get().getParent().isPresent() && action != NationRank.Action.CREATE_NATION) {
                TownMessage.warn(player, "You must be part of a nation to do this command.");
                return CommandResult.empty();
            } else n = res.town().get().getParent().get(); // nation confirmed
        }

        if ( isActionRelevant() ) {
            if (res.can(action) || player.hasPermission(action.permission())) {
                return this.townsExecute(n, t, res, player, args); // action confirmed, execute
            } else {
                TownMessage.warn(player, "Your rank does not permit you to do this."); // action not confirmed, do not execute
                return CommandResult.empty();
            }
        } else {
            return this.townsExecute(n, t, res, player, args); // action irrelevant, execute anyway
        }
    }

    public List<AbstractCommand> getChildren() {
        return children;
    }
}
