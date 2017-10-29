package com.atherys.towns.commands;

import com.atherys.towns.Settings;
import com.atherys.towns.nation.Nation;
import com.atherys.towns.resident.Resident;
import com.atherys.towns.town.Town;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextStyles;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class TownsMasterCommand extends TownsSimpleCommand {

    protected Map<List<String>, CommandCallable> children = new LinkedHashMap<>();

    public Map<List<String>, CommandCallable> getChildren() {
        return children;
    }

    protected void addChild(CommandCallable callable, String... aliases) {
        children.put(Arrays.asList(aliases), callable);
    }

    @Override
    protected CommandResult execute(Player player, CommandContext args, Resident resident, @Nullable Town town, @Nullable Nation nation) {
        for (Map.Entry<List<String>, CommandCallable> entry : getChildren().entrySet() ) {
            Text helpMsg = Text.builder()
                    .append(Text.of(TextStyles.BOLD, Settings.PRIMARY_COLOR, entry.getValue().getUsage(player)))
                    .onHover(TextActions.showText(
                            entry.getValue().getHelp(player).orElse(Text.of("Help Unavailable"))
                    ))
                    .onClick(TextActions.suggestCommand(entry.getValue().getUsage(player).toPlain()))
                    .build();
            player.sendMessage(helpMsg);
        }

        return CommandResult.empty();
    }

}
