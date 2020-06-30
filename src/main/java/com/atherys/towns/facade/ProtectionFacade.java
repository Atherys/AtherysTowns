package com.atherys.towns.facade;

import com.google.inject.Singleton;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.projectile.arrow.Arrow;
import org.spongepowered.api.event.cause.entity.damage.source.IndirectEntityDamageSource;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;

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

    private final List<ItemType> combatItems = Arrays.asList(
            ItemTypes.BOW,
            ItemTypes.SHIELD
    );

    private final List<EntityType> placeableEntities = Arrays.asList(
            EntityTypes.ARMOR_STAND,
            EntityTypes.BOAT,
            EntityTypes.CHESTED_MINECART,
            EntityTypes.COMMANDBLOCK_MINECART,
            EntityTypes.FURNACE_MINECART,
            EntityTypes.HOPPER_MINECART,
            EntityTypes.MOB_SPAWNER_MINECART,
            EntityTypes.RIDEABLE_MINECART,
            EntityTypes.TNT_MINECART,
            EntityTypes.ITEM_FRAME
    );

    private String getBlockString(BlockType blockType) {
        return blockType.getName().split(":")[1];
    }

    public boolean isCombatItem(ItemType itemType) {
        return combatItems.contains(itemType);
    }

    public boolean isPlaceableEntity(EntityType entityType) {
        return placeableEntities.contains(entityType);
    }

    public boolean isNonPlayerTarget(DamageEntityEvent event, IndirectEntityDamageSource src) {
        return (src.getSource() instanceof Arrow)
                && (src.getIndirectSource() instanceof Player)
                && !(event.getTargetEntity() instanceof Player);
    }

    public boolean isRedstone(BlockType blockType) {
        List<String> conquestBlocks = Arrays.asList("lever", "button");
        if (redstoneItems.contains(blockType)) {
            return true;
        }
        return conquestBlocks.contains(getBlockString(blockType)) && blockType.getTrait("powered").isPresent();
    }

    public boolean isChest(BlockType blockType) {
        return chests.contains(blockType);
    }

    public boolean isDoor(BlockType blockType) {
        List<String> conquestBlocks = Arrays.asList("door", "gate");
        if (doors.contains(blockType)) {
            return true;
        }
        return conquestBlocks.stream().anyMatch(getBlockString(blockType)::contains) && blockType.getTrait("open").isPresent();
    }
}
