package com.atherys.towns.utils;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializers;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class WildernessFilter implements TypeSerializer<WildernessFilter> {

    public static class FilterNode implements TypeSerializer<FilterNode> {

        private Map<BlockType, Double> blocks = new HashMap<>();

        static {
            TypeSerializers.getDefaultSerializers().registerType( TypeToken.of( FilterNode.class ), FilterNode.empty() );
        }

        public FilterNode ( BlockType altBlock, double percentage ) {
            this.blocks.put( altBlock, percentage );
        }

        private FilterNode () {
        }

        public static FilterNode empty () {
            return new FilterNode();
        }

        public double getChance ( BlockType type ) {
            return blocks.getOrDefault( type.getName(), 0.0 );
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

        @Override
        public FilterNode deserialize ( TypeToken<?> type, ConfigurationNode value ) throws ObjectMappingException {
            FilterNode node = FilterNode.empty();
            for ( ConfigurationNode child : value.getChildrenList() ) {
                Optional<BlockType> block = Sponge.getRegistry().getType( BlockType.class, child.getKey().toString() );
                if ( block.isPresent() ) {
                    node.add( block.get(), child.getDouble() );
                } else {
                    throw new ObjectMappingException( child.getKey() + " is not a valid BlockType." );
                }
            }
            return node;
        }

        @Override
        public void serialize ( TypeToken<?> type, FilterNode obj, ConfigurationNode value ) throws ObjectMappingException {
            obj.blocks.forEach( ( k, v ) -> value.getNode( k.getName() ).setValue( v ) );
        }
    }

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

    @Override
    public WildernessFilter deserialize ( TypeToken<?> type, ConfigurationNode value ) throws ObjectMappingException {
        WildernessFilter filter = new WildernessFilter();
        for ( ConfigurationNode child : value.getChildrenList() ) {
            Optional<BlockType> block = Sponge.getRegistry().getType( BlockType.class, child.getKey().toString() );
            if ( block.isPresent() ) {
                filter.filter.put( block.get(), child.getValue( TypeToken.of( FilterNode.class ) ) );
            } else {
                throw new ObjectMappingException( child.getKey() + " is not a valid BlockType." );
            }
        }

        return filter;
    }

    @Override
    public void serialize ( TypeToken<?> type, WildernessFilter obj, ConfigurationNode value ) throws ObjectMappingException {
        for ( Map.Entry<BlockType, FilterNode> nodes : obj.filter.entrySet() ) {
            value.getNode( nodes.getKey().getName() ).setValue( TypeToken.of( FilterNode.class ), nodes.getValue() );
        }
    }
}
