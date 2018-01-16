package com.atherys.towns;

import com.atherys.core.database.mongo.MongoDatabaseConfig;
import com.atherys.core.utils.PluginConfig;
import com.atherys.towns.permissions.ranks.NationRank;
import com.atherys.towns.permissions.ranks.NationRanks;
import com.atherys.towns.permissions.ranks.TownRank;
import com.atherys.towns.permissions.ranks.TownRanks;
import com.atherys.towns.utils.WildernessFilter;
import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializers;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public final class TownsConfig extends PluginConfig {

    @Setting ( "defaultConfig" )
    public boolean DEFAULT = true;

    @Setting ( "colors" )
    public ColorConfig COLORS = new ColorConfig();

    @ConfigSerializable
    public static class ColorConfig {

        @Setting( "decoration" )
        public TextColor DECORATION = TextColors.GOLD;

        @Setting( "primary" )
        public TextColor PRIMARY = TextColors.DARK_GREEN;

        @Setting( "secondary" )
        public TextColor SECONDARY = TextColors.GREEN;

        @Setting( "tertiary" )
        public TextColor TERTIARY = TextColors.AQUA;

        @Setting( "text" )
        public TextColor TEXT = TextColors.YELLOW;

        @Setting( "warning" )
        public TextColor WARNING = TextColors.RED;
    }

    @Setting ( "towns" )
    public TownConfig TOWN = new TownConfig();

    @ConfigSerializable
    public static class TownConfig {

        @Setting ( "border_update_rate" )
        public int BORDER_UPDATE_RATE = 4;

        @Setting ( "npc_name" )
        public String NPC_NAME = "NPC";

        @Setting ( "max_name_length" )
        public int MAX_NAME_LENGTH = 20;

        @Setting ( "max_plot_area" )
        public int MAX_PLOT_AREA = 512;

        @Setting ( "min_plot_size" )
        public int MIN_PLOT_SIZE = 16;

        @Setting ( "initial_area" )
        public int INITIAL_AREA = 5120;

        @Setting ( "spawn_delay" )
        public int SPAWN_DELAY = 10;

        @Setting ( "town_leader" )
        public TownRank TOWN_LEADER = TownRanks.MAYOR;

        @Setting ( "nation_leader" )
        public NationRank NATION_LEADER = NationRanks.LEADER;

    }

    @Setting ( "titles" )
    public TitleConfig TITLES = new TitleConfig();

    @ConfigSerializable
    public static class TitleConfig {

        @Setting ( "fade_in_ticks" )
        public int FADE_IN = 5;

        @Setting ( "stay_ticks" )
        public int STAY = 10;

        @Setting ( "fade_out_ticks" )
        public int FADE_OUT = 5;

        @Setting ( "subtitle_fade_in_ticks" )
        public int SUB_FADE_IN = 5;

        @Setting ( "subtitle_stay_ticks" )
        public int SUB_STAY = 10;

        @Setting ( "subtitle_fade_out_ticks" )
        public int SUB_FADE_OUT = 5;

    }

    @Setting ( "database" )
    public MongoDatabaseConfig DATABASE = new MongoDatabaseConfig();

    @Setting ( "wilderness_regen" )
    public WildernessConfig WILDERNESS_REGEN = new WildernessConfig();

    @ConfigSerializable
    public static class WildernessConfig {

        @Setting ( "enabled" )
        public boolean ENABLED = true;

        @Setting ( "rate" )
        public int RATE = 60;

        @Setting ( "time_units" )
        public TimeUnit UNIT = TimeUnit.SECONDS;

        @Setting ( "filter" )
        public WildernessFilter FILTER = new WildernessFilter();
        {
            WildernessFilter.FilterNode ironOreFilter = WildernessFilter.FilterNode.empty();
            ironOreFilter.add( BlockTypes.STONE, 0.5 );
            ironOreFilter.add( BlockTypes.IRON_ORE, 0.5 );
            FILTER.set( BlockTypes.IRON_ORE, ironOreFilter );
        }

        @Setting ( "last_regen" )
        public long LAST = 0;

    }

    @Setting ( "switch_flag_blocks" )
    public List<BlockType> SWITCH_FLAG_BLOCKS = new ArrayList<>();
    {
        SWITCH_FLAG_BLOCKS.add(BlockTypes.CHEST);
        SWITCH_FLAG_BLOCKS.add(BlockTypes.ENDER_CHEST);
        SWITCH_FLAG_BLOCKS.add(BlockTypes.TRAPPED_CHEST);
        SWITCH_FLAG_BLOCKS.add(BlockTypes.ACACIA_DOOR);
        SWITCH_FLAG_BLOCKS.add(BlockTypes.BIRCH_DOOR);
        SWITCH_FLAG_BLOCKS.add(BlockTypes.DARK_OAK_DOOR);
        SWITCH_FLAG_BLOCKS.add(BlockTypes.IRON_DOOR);
        SWITCH_FLAG_BLOCKS.add(BlockTypes.JUNGLE_DOOR);
        SWITCH_FLAG_BLOCKS.add(BlockTypes.SPRUCE_DOOR);
        SWITCH_FLAG_BLOCKS.add(BlockTypes.WOODEN_DOOR);
        SWITCH_FLAG_BLOCKS.add(BlockTypes.TRAPDOOR);
        SWITCH_FLAG_BLOCKS.add(BlockTypes.LEVER);
        SWITCH_FLAG_BLOCKS.add(BlockTypes.STONE_BUTTON);
        SWITCH_FLAG_BLOCKS.add(BlockTypes.WOODEN_BUTTON);
    }

    TownsConfig( ) throws IOException {
        super( AtherysTowns.getInstance().getWorkingDirectory(), "config.conf" );

        TypeSerializers.getDefaultSerializers().registerType( TypeToken.of(WildernessFilter.FilterNode.class), WildernessFilter.FilterNode.empty() );
        TypeSerializers.getDefaultSerializers().registerType( TypeToken.of(WildernessFilter.class), new WildernessFilter() );
    }
}

