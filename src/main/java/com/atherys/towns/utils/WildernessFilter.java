package com.atherys.towns.utils;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;

import java.util.HashMap;
import java.util.Map;

@ConfigSerializable
public class WildernessFilter {

    @ConfigSerializable
    public static class FilterNode {

        @Setting
        private Map<BlockType, Double> blocks = new HashMap<>();

        public FilterNode ( BlockType altBlock, double percentage ) {
            this.blocks.put( altBlock, percentage );
        }

        private FilterNode () {
        }

        public static FilterNode of( BlockType altBlock, double percentage ) {
            return new FilterNode( altBlock, percentage );
        }

        public static FilterNode empty () {
            return new FilterNode();
        }

        public double getChance ( BlockType type ) {
            return blocks.getOrDefault( type, 0.0 );
        }

        public void add ( BlockType block, double percent ) {
            this.blocks.put( block, percent );
        }

        // Changes the BlockType of the given snapshot to an appropriate one from the FilterNode
        public BlockSnapshot getFinal ( BlockSnapshot original ) {
            BlockSnapshot finalSnap = original;
            double r = Math.random();

            for ( Map.Entry<BlockType, Double> entry : blocks.entrySet() ) {

                if ( r <= entry.getValue() ) {
                    if ( entry.getKey().equals( original.getState().getType() ) ) continue;
                    finalSnap = BlockSnapshot.builder().from( original ).blockState( BlockState.builder().blockType( entry.getKey() ).build() ).build();
                }
            }
            return finalSnap;
        }
    }

    @Setting
    private Map<BlockType, FilterNode> filter = new HashMap<>();

    public WildernessFilter () {
    }

    public void set ( BlockType blockType, FilterNode node ) {
        this.filter.put( blockType, node );
    }

    public boolean has ( BlockType blockType ) {
        return filter.containsKey( blockType );
    }

    public FilterNode getAlternatives ( String blockId ) {
        return filter.getOrDefault( blockId, FilterNode.empty() );
    }
}
