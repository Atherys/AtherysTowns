package com.atherys.towns;

import com.atherys.core.database.mongo.MongoDatabaseConfig;
import com.atherys.core.utils.PluginConfig;
import com.atherys.towns.permissions.actions.NationAction;
import com.atherys.towns.permissions.actions.NationActions;
import com.atherys.towns.permissions.actions.TownAction;
import com.atherys.towns.permissions.actions.TownActions;
import com.atherys.towns.utils.WildernessFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;

public final class TownsConfig extends PluginConfig {

    @Setting("defaultConfig")
    public boolean DEFAULT = true;

    @Setting("colors")
    public ColorConfig COLORS = new ColorConfig();

    @ConfigSerializable
    public static class ColorConfig {

        @Setting("decoration")
        public TextColor DECORATION = TextColors.GOLD;

        @Setting("primary")
        public TextColor PRIMARY = TextColors.DARK_GREEN;

        @Setting("secondary")
        public TextColor SECONDARY = TextColors.GREEN;

        @Setting("tertiary")
        public TextColor TERTIARY = TextColors.AQUA;

        @Setting("text")
        public TextColor TEXT = TextColors.YELLOW;

        @Setting("warning")
        public TextColor WARNING = TextColors.RED;
    }

    @Setting("towns")
    public TownConfig TOWN = new TownConfig();

    @ConfigSerializable
    public static class TownConfig {

        @Setting("date_format")
        public String DATE_FORMAT = "dd-MM-yyyy @ HH:mm";

        @Setting("border_update_rate")
        public int BORDER_UPDATE_RATE = 4;

        @Setting("npc_name")
        public String NPC_NAME = "NPC";

        @Setting("max_name_length")
        public int MAX_NAME_LENGTH = 20;

        @Setting("max_plot_area")
        public int MAX_PLOT_AREA = 512;

        @Setting("min_plot_size")
        public int MIN_PLOT_SIZE = 16;

        @Setting("initial_area")
        public int INITIAL_AREA = 5120;

        @Setting("spawn_delay")
        public int SPAWN_DELAY = 10;

        @Setting("town_ranks")
        public TownRanksConfig TOWN_RANKS = new TownRanksConfig();

        @ConfigSerializable
        public static class TownRanksConfig {

            @Setting("none")
            public List<TownAction> NONE = Arrays.asList(
                TownActions.CREATE_TOWN,
                TownActions.JOIN_TOWN
            );

            @Setting("resident")
            public List<TownAction> RESIDENT = Arrays.asList(
                TownActions.CHAT,
                TownActions.JOIN_TOWN,
                TownActions.LEAVE_TOWN,
                TownActions.TOWN_DEPOSIT
            );

            @Setting("citizen")
            public List<TownAction> CITIZEN = Arrays.asList(
                TownActions.CHAT,
                TownActions.JOIN_TOWN,
                TownActions.LEAVE_TOWN,
                TownActions.TOWN_DEPOSIT
            );

            @Setting("assistant")
            public List<TownAction> ASSISTANT = Arrays.asList(
                TownActions.CHAT,
                TownActions.JOIN_TOWN,
                TownActions.LEAVE_TOWN,
                TownActions.INVITE_PLAYER,
                TownActions.KICK_PLAYER,
                TownActions.SET_MOTD,
                TownActions.SET_DESCRIPTION,
                TownActions.SET_COLOR,
                TownActions.SHOW_TOWN_BORDER,
                TownActions.TOWN_DEPOSIT
            );

            @Setting("CO_MAYOR")
            public List<TownAction> CO_MAYOR = Arrays.asList(
                TownActions.CHAT,
                TownActions.INVITE_PLAYER,
                TownActions.KICK_PLAYER,
                TownActions.LEAVE_TOWN,
                TownActions.SET_MOTD,
                TownActions.SET_DESCRIPTION,
                TownActions.SET_COLOR,
                TownActions.SET_NAME,
                TownActions.SET_RANK,
                TownActions.CLAIM_PLOT,
                TownActions.UNCLAIM_PLOT,
                TownActions.SET_FLAGS,
                TownActions.SET_FLAG_PVP,
                TownActions.SHOW_TOWN_BORDER,
                TownActions.TOWN_DEPOSIT,
                TownActions.TOWN_WITHDRAW,
                TownActions.MODIFY_PLOT_FLAG,
                TownActions.MODIFY_PLOT_NAME
            );

            @Setting("MAYOR")
            public List<TownAction> MAYOR = Arrays.asList(
                TownActions.CHAT,
                TownActions.INVITE_PLAYER,
                TownActions.KICK_PLAYER,
                TownActions.SET_MOTD,
                TownActions.SET_DESCRIPTION,
                TownActions.SET_COLOR,
                TownActions.CLAIM_PLOT,
                TownActions.UNCLAIM_PLOT,
                TownActions.SET_NAME,
                TownActions.SET_RANK,
                TownActions.SET_MAYOR,
                TownActions.SET_FLAGS,
                TownActions.SET_FLAG_PVP,
                TownActions.SET_FLAG_BUILD,
                TownActions.SET_FLAG_DESTROY,
                TownActions.SET_FLAG_JOIN,
                TownActions.SET_FLAG_SWITCH,
                TownActions.SET_FLAG_DAMAGE_ENTITY,
                TownActions.RUIN_TOWN,
                TownActions.SHOW_TOWN_BORDER,
                TownActions.TOWN_DEPOSIT,
                TownActions.TOWN_WITHDRAW,
                TownActions.MODIFY_PLOT_FLAG,
                TownActions.MODIFY_PLOT_NAME
            );

        }

        @Setting("nation_ranks")
        public NationRanksConfig NATION_RANKS = new NationRanksConfig();

        @ConfigSerializable
        public static class NationRanksConfig {

            @Setting("none")
            public List<NationAction> NONE = Collections.emptyList();

            @Setting("resident")
            public List<NationAction> RESIDENT = Arrays.asList(
                NationActions.CHAT,
                NationActions.NATION_DEPOSIT
            );

            @Setting("co_leader")
            public List<NationAction> CO_LEADER = Arrays.asList(
                NationActions.CHAT,
                NationActions.NATION_DEPOSIT,
                NationActions.NATION_WITHDRAW
            );

            @Setting("leader")
            public List<NationAction> LEADER = Arrays.asList(
                NationActions.CHAT,
                NationActions.NATION_DEPOSIT,
                NationActions.NATION_WITHDRAW,
                NationActions.SET_COLOR,
                NationActions.SET_DESCRIPTION,
                NationActions.SET_NAME,
                NationActions.SET_RANK,
                NationActions.SET_LEADER_TITLE
            );

        }

    }

    @Setting("titles")
    public TitleConfig TITLES = new TitleConfig();

    @ConfigSerializable
    public static class TitleConfig {

        @Setting("fade_in_ticks")
        public int FADE_IN = 5;

        @Setting("stay_ticks")
        public int STAY = 10;

        @Setting("fade_out_ticks")
        public int FADE_OUT = 5;

        @Setting("subtitle_fade_in_ticks")
        public int SUB_FADE_IN = 5;

        @Setting("subtitle_stay_ticks")
        public int SUB_STAY = 10;

        @Setting("subtitle_fade_out_ticks")
        public int SUB_FADE_OUT = 5;

    }

    @Setting("database")
    public MongoDatabaseConfig DATABASE = new MongoDatabaseConfig();

    @Setting("wilderness_regen")
    public WildernessConfig WILDERNESS_REGEN = new WildernessConfig();

    @ConfigSerializable
    public static class WildernessConfig {

        @Setting("enabled")
        public boolean ENABLED = true;

        @Setting("rate")
        public int RATE = 60;

        @Setting("time_units")
        public TimeUnit UNIT = TimeUnit.SECONDS;

        @Setting("filter")
        public WildernessFilter FILTER = new WildernessFilter();

        {
            WildernessFilter.FilterNode ironOreFilter = WildernessFilter.FilterNode.empty();
            ironOreFilter.add(BlockTypes.STONE, 0.5);
            ironOreFilter.add(BlockTypes.IRON_ORE, 0.5);
            FILTER.set(BlockTypes.IRON_ORE, ironOreFilter);
        }

        @Setting("last_regen")
        public long LAST = 0;

    }

    @Setting("switch_flag_blocks")
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

    TownsConfig() throws IOException {
        super(AtherysTowns.getInstance().getWorkingDirectory(), "config.conf");
    }
}

