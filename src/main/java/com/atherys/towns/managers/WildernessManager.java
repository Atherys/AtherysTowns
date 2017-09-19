package com.atherys.towns.managers;

import org.spongepowered.api.block.BlockType;

import java.util.HashMap;
import java.util.Map;

public final class WildernessManager {

    //private static final Random random = new Random();
//
public static class WildernessRegenFilter {

    public static class RegenData {
        float percent;
        String alt;

        public RegenData(float percent, String alt) {
            this.percent = percent;
            this.alt = alt;
        }
    }

    private Map<String,RegenData> filter = new HashMap<>();

    public boolean hasItem (BlockType type) {
        return filter.containsKey(type.getName());
    }

    public void addItem ( String name, RegenData data ) {
        filter.put(name, data);
    }

    public RegenData getRegenData ( BlockType type ) {
        return filter.getOrDefault(type.getName(), new RegenData(100, "minecraft:stone") );
    }

}
//
    //public static void setup() {
    //    DatabaseManager.createWildernessRegenTable();
    //}
//
    //public static void task() {
    //    List<BlockSnapshot> snaps = DatabaseManager.loadSnapshotsBeforeTimestamp(System.currentTimeMillis());
//
    //    if (snaps != null) {
    //        for ( BlockSnapshot snap : snaps ) {
    //            Optional<Location<World>> loc = snap.getLocation();
//
    //            if ( loc.isPresent() ) {
//
    //                Optional<Town> town = AtherysTowns.getInstance().getTownManager().getByLocation(loc.get());
    //                if ( town.isPresent() ) continue;
//
    //                snap.restore(true, BlockChangeFlag.ALL);
    //            }
    //        }
    //    }
    //}
//
    //public static void setItemRegenInfo (BlockType type, float rate, BlockType regen) {
    //    Settings.WILDERNESS_REGEN_FILTER.addItem( type.getName(), new WildernessRegenFilter.RegenData( rate, regen.getName() ) );
    //}
//
    //public static WildernessRegenFilter.RegenData getItemRegenInfo (BlockType type) {
    //    return Settings.WILDERNESS_REGEN_FILTER.getRegenData(type);
    //}
//
    //public static boolean isItemRegenerable ( BlockType type ) {
    //    if ( type.equals(BlockTypes.AIR) ) return true;
    //    return Settings.WILDERNESS_REGEN_FILTER.hasItem(type);
    //}
//
    //public static BlockSnapshot getRegenSnapshot(ChangeBlockEvent event, BlockSnapshot snap ) {
    //    if ( event instanceof ChangeBlockEvent.Place ) return snap;
//
    //    WildernessRegenFilter.RegenData info = getItemRegenInfo(snap.getExtendedState().getType());
//
    //    if ( getRandomPercentage() <= info.percent ) {
    //        return snap;
    //    } else {
    //        Optional<BlockType> type = Sponge.getRegistry().getType( BlockType.class, info.alt );
    //        if ( !type.isPresent() ) type = Optional.of(BlockTypes.STONE);
//
    //        BlockState state = BlockState.builder().blockType( type.get() ).build();
    //        return snap.withState(state);
    //    }
    //}
//
    //public static double getRandomPercentage() {
    //    return 100.0d * random.nextDouble();
    //}


}
