package com.atherys.towns.facade;

import com.google.inject.Singleton;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

@Singleton
public class ProtectionFacade {
    private final List<BlockType> doors = Arrays.asList(
            BlockTypes.BIRCH_DOOR,
            BlockTypes.DARK_OAK_DOOR,
            BlockTypes.ACACIA_DOOR,
            BlockTypes.IRON_DOOR,
            BlockTypes.JUNGLE_DOOR,
            BlockTypes.SPRUCE_DOOR,
            BlockTypes.WOODEN_DOOR,
            BlockTypes.TRAPDOOR,
            BlockTypes.IRON_TRAPDOOR,
            BlockTypes.BIRCH_FENCE_GATE,
            BlockTypes.FENCE_GATE,
            BlockTypes.ACACIA_FENCE_GATE,
            BlockTypes.DARK_OAK_FENCE_GATE,
            BlockTypes.JUNGLE_FENCE_GATE,
            BlockTypes.SPRUCE_FENCE_GATE
    );

    private final List<BlockType> chests = Arrays.asList(
            BlockTypes.CHEST,
            BlockTypes.ENDER_CHEST,
            BlockTypes.TRAPPED_CHEST
    );

    private final List<BlockType> redstoneItems = Arrays.asList(
            BlockTypes.LEVER,
            BlockTypes.STONE_BUTTON,
            BlockTypes.WOODEN_BUTTON,
            BlockTypes.HEAVY_WEIGHTED_PRESSURE_PLATE,
            BlockTypes.LIGHT_WEIGHTED_PRESSURE_PLATE,
            BlockTypes.STONE_PRESSURE_PLATE,
            BlockTypes.WOODEN_PRESSURE_PLATE,
            BlockTypes.HOPPER,
            BlockTypes.TRIPWIRE,
            BlockTypes.DISPENSER
    );

    private final List<ItemType> useItems = Arrays.asList(
            ItemTypes.WATER_BUCKET,
            ItemTypes.LAVA_BUCKET,
            ItemTypes.FLINT_AND_STEEL,
            ItemTypes.BOAT,
            ItemTypes.MINECART,
            ItemTypes.CHEST_MINECART,
            ItemTypes.COMMAND_BLOCK_MINECART,
            ItemTypes.FURNACE_MINECART,
            ItemTypes.HOPPER_MINECART,
            ItemTypes.TNT_MINECART,
            ItemTypes.BUCKET,
            ItemTypes.DYE,
            ItemTypes.FISHING_ROD,
            ItemTypes.LEAD,
            ItemTypes.SHEARS,
            ItemTypes.SNOWBALL,
            ItemTypes.ENDER_PEARL,
            ItemTypes.ENDER_EYE,
            ItemTypes.FIRE_CHARGE,
            ItemTypes.FIREWORK_CHARGE
    );

    public boolean isUseItem(Player player) {
        ItemType itemType = player.getItemInHand(HandTypes.MAIN_HAND).map(ItemStack::getType).orElse(ItemTypes.AIR);
        return useItems.contains(itemType);
    }

    public boolean isRedstone(BlockType blockType) {
        List<String> conquestBlocks = Arrays.asList("lever", "button");
        if (redstoneItems.contains(blockType)) {
            return true;
        }
        String blockName = blockType.getName().split(":")[1];
        return conquestBlocks.contains(blockName) && blockType.getTrait("powered").isPresent();
    }

    public boolean isChest(BlockType blockType) {
        return chests.contains(blockType);
    }

    public boolean isDoor(BlockType blockType) {
        List<String> conquestBlocks = Arrays.asList("door", "gate");
        if (doors.contains(blockType)) {
            return true;
        }
        String blockName = blockType.getName().split(":")[1];
        return conquestBlocks.stream().anyMatch(blockName::contains) && blockType.getTrait("open").isPresent();
    }
}
