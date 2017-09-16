package com.atherys.towns.managers;

import com.atherys.towns.AtherysTowns;
import com.atherys.towns.Settings;
import com.atherys.towns.db.DatabaseManager;
import com.atherys.towns.town.Town;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.util.Tuple;
import org.spongepowered.api.world.BlockChangeFlag;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.List;
import java.util.Optional;
import java.util.Random;

public final class WildernessManager {

    private static final Random random = new Random();

    public static void setup() {
        DatabaseManager.createWildernessRegenTable();
    }

    public static void task() {
        List<BlockSnapshot> snaps = DatabaseManager.loadSnapshotsBeforeTimestamp(System.currentTimeMillis());

        if (snaps != null) {
            for ( BlockSnapshot snap : snaps ) {
                Optional<Location<World>> loc = snap.getLocation();

                if ( loc.isPresent() ) {

                    Optional<Town> town = AtherysTowns.getInstance().getTownManager().getByLocation(loc.get());
                    if ( town.isPresent() ) continue;

                    snap.restore(true, BlockChangeFlag.ALL);
                }
            }
        }
    }

    public static void setItemRegenInfo (BlockType type, double rate, BlockType regen) {
        Settings.WILDERNESS_REGEN_FILTER.put(type.getName(), Tuple.of(rate, regen.getName()));
    }

    public static Tuple<Double, String> getItemRegenInfo (BlockType type) {
        return Settings.WILDERNESS_REGEN_FILTER.getOrDefault(type.getName(), Tuple.of(Settings.DEFAULT_REGEN_RATE, Settings.DEFAULT_REGEN_MATERIAL) );
    }

    public static void removeItem ( BlockType type ) {
        Settings.WILDERNESS_REGEN_FILTER.remove(type.getName());
    }

    public static boolean isItemRegenerable ( BlockType type ) {
        if ( type.equals(BlockTypes.AIR) ) return true;
        return Settings.WILDERNESS_REGEN_FILTER.containsKey(type.getName());
    }

    public static BlockSnapshot getRegenSnapshot(ChangeBlockEvent event, BlockSnapshot snap ) {
        if ( event instanceof ChangeBlockEvent.Place ) return snap;

        Tuple<Double,String> info = getItemRegenInfo(snap.getExtendedState().getType());

        if ( getRandomPercentage() <= info.getFirst() ) {
            return snap;
        } else {
            Optional<BlockType> type = Sponge.getRegistry().getType( BlockType.class, info.getSecond() );
            if ( !type.isPresent() ) type = Optional.of(BlockTypes.STONE);

            BlockState state = BlockState.builder().blockType( type.get() ).build();
            return snap.withState(state);
        }
    }

    public static double getRandomPercentage() {
        return 100.0d * random.nextDouble();
    }


}
