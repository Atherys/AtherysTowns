package com.atherys.towns.commands.plot;

import com.atherys.towns.messaging.TownMessage;
import com.atherys.towns.nation.Nation;
import com.atherys.towns.resident.Resident;
import com.atherys.towns.town.Town;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.util.Color;

import javax.annotation.Nullable;
import java.util.Arrays;

public class PlotToolCommand extends AbstractPlotCommand {

    private static final ItemStack PLOT_SELECTOR_TOOL = ItemStack.builder()
            .itemType(ItemTypes.STONE_AXE)
            .quantity(1)
            .keyValue( Keys.DISPLAY_NAME, Text.of(TextStyles.BOLD, "Plot Selector") )
            .keyValue( Keys.ITEM_LORE, Arrays.asList( Text.of("Use this tool to create a plot definition"), Text.of("by left-clicking at the first location, "), Text.of("and right-clicking at the second.") ) )
            .keyValue( Keys.CAN_DROP_AS_ITEM, false )
            .keyValue( Keys.COLOR, Color.BLUE)
            .build();

    PlotToolCommand() {
        super(
                new String[] {"tool"},
                "tool",
                Text.of("Used to get the plot selector tool, required for claiming new plots")
        );
    }

    @Override
    public CommandResult townsExecute(@Nullable Nation nation, @Nullable Town town, Resident resident, Player player, CommandContext args) {
        Inventory pInv = player.getInventory();
        if ( !pInv.contains(PLOT_SELECTOR_TOOL) ) {
            pInv.offer(PLOT_SELECTOR_TOOL.copy());
        } else {
            TownMessage.warn(player, "You already have the plot selector tool in your inventory!");
        }
        return CommandResult.empty();
    }

    @Override
    public CommandSpec getSpec() {
        return CommandSpec.builder()
                .permission("atherys.towns.commands.plot.tool")
                .description(Text.of("Used to get the tool with which one can select 2 locations in order to define a plot."))
                .executor(this)
                .build();
    }

    public static ItemStack plotSelector() { return PLOT_SELECTOR_TOOL.copy(); }
}
