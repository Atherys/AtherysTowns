package com.atherys.towns;

import com.atherys.core.utils.PluginConfig;
import com.atherys.towns.permissions.ranks.NationRank;
import com.atherys.towns.permissions.ranks.NationRanks;
import com.atherys.towns.permissions.ranks.TownRank;
import com.atherys.towns.permissions.ranks.TownRanks;
import com.google.gson.JsonObject;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public final class TownsConfig extends PluginConfig {


    @Setting ( "colors" )
    public ColorConfig COLORS = new ColorConfig();

    @ConfigSerializable
    private static class ColorConfig {

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
    private static class TownConfig {

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

    }

    @Setting ( "titles" )
    public TitleConfig TITLES = new TitleConfig();

    private static class TitleConfig {

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
    public DatabaseConfig DATABASE = new DatabaseConfig();

    @ConfigSerializable
    private static class DatabaseConfig {

        @Setting( "host" )
        public String HOST = "localhost";

        @Setting( "port" )
        public int PORT = 27017;

        @Setting( "name" )
        public String NAME = "core_Database";

        @Setting( "userDb" )
        public String USER_DB = "user_Database";

        @Setting( "username" )
        public String USERNAME = "username";

        @Setting( "password" )
        public String PASSWORD = "password";

    }

    @Setting ( "wilderness_regen" )
    public WildernessConfig WILDERNESS_REGEN = new WildernessConfig();

    @ConfigSerializable
    private static class WildernessConfig {

        @Setting ( "enabled" )
        public boolean ENABLED = true;

        @Setting ( "rate" )
        public int RATE = 60;

        @Setting ( "time_units" )
        public TimeUnit UNIT = TimeUnit.SECONDS;

        @Setting ( "filter" )
        public Map<String,Map<String,Double>> FILTER = new HashMap<>();

    }

    public static TextColor             DECORATION_COLOR =              TextColors.GOLD;
    public static TextColor             PRIMARY_COLOR =                 TextColors.DARK_GREEN;
    public static TextColor             SECONDARY_COLOR =               TextColors.GREEN;
    public static TextColor             TERTIARY_COLOR =                TextColors.AQUA;
    public static TextColor             TEXT_COLOR =                    TextColors.YELLOW;
    public static TextColor             WARNING_COLOR =                 TextColors.RED;
    public static TextColor             TOWN_CHAT_COLOR =               TextColors.DARK_AQUA;
    public static int                   TOWN_BORDER_UPDATE_RATE =       4;
    public static String                NON_PLAYER_CHARACTER_NAME =     "NPC";
    public static int                   MAX_TOWN_NAME_LENGTH =          4;
    public static int                   MAX_PLOT_AREA =                 512;
    public static int                   MIN_SIZE_PLOT_SIDE =            16;
    public static int                   INITIAL_TOWN_AREA_LIMIT =       5120;
    public static int                   TITLE_FADEIN_TICKS =            5;
    public static int                   TITLE_STAY_TICKS =              10;
    public static int                   TITLE_FADEOUT_TICKS =           5;
    public static int                   SUBTITLE_FADEIN_TICKS =         5;
    public static int                   SUBTITLE_STAY_TICKS =           10;
    public static int                   SUBTITLE_FADEOUT_TICKS =        5;
    public static String                DB_HOST =                       "localhost";
    public static int                   DB_PORT =                       27017;
    public static String                DB_DATABASE =                   "towns_database";
    public static String                DB_USER_DB =                    "admin";
    public static String                DB_USER =                       "user";
    public static String                DB_PASSWORD =                   "password";
    public static int                   TOWN_SPAWN_DELAY =              10;

    public static boolean               WILDERNESS_REGEN_ENABLED =      true;
    public static int                   WILDERNESS_REGEN_RATE =         60;
    public static TimeUnit              WILDERNESS_REGEN_RATE_UNIT =    TimeUnit.SECONDS;

    public static JsonObject            WILDERNESS_REGEN_FILTER;
    public static List<String>          SWITCH_FLAG_BLOCKS =            new ArrayList<>();

    public static NationRank NATION_LEADER_RANK = NationRanks.LEADER;
    public static TownRank TOWN_LEADER_RANK = TownRanks.MAYOR;

    //public static Map<TownRank,List<TownsActionOld>>   TOWN_RANK_PERMISSIONS = new HashMap<>();
    //static {
    //   TOWN_RANK_PERMISSIONS.put(TownRank.NONE, Arrays.asList(
    //           TownRank.Action.NULL,
    //           TownRank.Action.CHAT,
    //           TownRank.Action.JOIN_TOWN,
    //           TownRank.Action.LEAVE_TOWN
    //   ));
    //   TOWN_RANK_PERMISSIONS.put(TownRank.RESIDENT, Arrays.asList(
    //           TownRank.Action.NULL,
    //           TownRank.Action.CHAT,
    //           TownRank.Action.JOIN_TOWN,
    //           TownRank.Action.LEAVE_TOWN,
    //           TownRank.Action.TOWN_DEPOSIT
    //   ));
    //   TOWN_RANK_PERMISSIONS.put(TownRank.CITIZEN, Arrays.asList(
    //           TownRank.Action.NULL,
    //           TownRank.Action.CHAT,
    //           TownRank.Action.JOIN_TOWN,
    //           TownRank.Action.LEAVE_TOWN,
    //           TownRank.Action.TOWN_DEPOSIT
    //   ));
    //   TOWN_RANK_PERMISSIONS.put(TownRank.ASSISTANT, Arrays.asList(
    //           TownRank.Action.NULL,
    //           TownRank.Action.CHAT,
    //           TownRank.Action.JOIN_TOWN,
    //           TownRank.Action.LEAVE_TOWN,
    //           TownRank.Action.INVITE_PLAYER,
    //           TownRank.Action.KICK_PLAYER,
    //           TownRank.Action.SET_MOTD,
    //           TownRank.Action.SET_DESCRIPTION,
    //           TownRank.Action.SET_COLOR,
    //           TownRank.Action.SHOW_TOWN_BORDER,
    //           TownRank.Action.TOWN_DEPOSIT
    //   ));
    //   TOWN_RANK_PERMISSIONS.put(TownRank.CO_MAYOR, Arrays.asList(
    //           TownRank.Action.NULL,
    //           TownRank.Action.CHAT,
    //           TownRank.Action.INVITE_PLAYER,
    //           TownRank.Action.KICK_PLAYER,
    //           TownRank.Action.LEAVE_TOWN,
    //           TownRank.Action.SET_MOTD,
    //           TownRank.Action.SET_DESCRIPTION,
    //           TownRank.Action.SET_COLOR,
    //           TownRank.Action.SET_NAME,
    //           TownRank.Action.SET_RANK,
    //           TownRank.Action.CLAIM_PLOT,
    //           TownRank.Action.UNCLAIM_PLOT,
    //           TownRank.Action.SET_FLAGS,
    //           TownRank.Action.SET_FLAG_PVP,
    //           TownRank.Action.SHOW_TOWN_BORDER,
    //           TownRank.Action.TOWN_DEPOSIT,
    //           TownRank.Action.TOWN_WITHDRAW,
    //           TownRank.Action.MODIFY_PLOT_FLAG,
    //           TownRank.Action.MODIFY_PLOT_NAME
    //   ));
    //   TOWN_RANK_PERMISSIONS.put(TownRank.MAYOR, Arrays.asList(
    //           TownRank.Action.NULL,
    //           TownRank.Action.CHAT,
    //           TownRank.Action.INVITE_PLAYER,
    //           TownRank.Action.KICK_PLAYER,
    //           TownRank.Action.SET_MOTD,
    //           TownRank.Action.SET_DESCRIPTION,
    //           TownRank.Action.SET_COLOR,
    //           TownRank.Action.CLAIM_PLOT,
    //           TownRank.Action.UNCLAIM_PLOT,
    //           TownRank.Action.SET_NAME,
    //           TownRank.Action.SET_RANK,
    //           TownRank.Action.SET_MAYOR,
    //           TownRank.Action.SET_FLAGS,
    //           TownRank.Action.SET_FLAG_PVP,
    //           TownRank.Action.SET_FLAG_BUILD,
    //           TownRank.Action.SET_FLAG_DESTROY,
    //           TownRank.Action.SET_FLAG_JOIN,
    //           TownRank.Action.SET_FLAG_SWITCH,
    //           TownRank.Action.SET_FLAG_DAMAGE_ENTITY,
    //           TownRank.Action.RUIN_TOWN,
    //           TownRank.Action.SHOW_TOWN_BORDER,
    //           TownRank.Action.TOWN_DEPOSIT,
    //           TownRank.Action.TOWN_WITHDRAW,
    //           TownRank.Action.MODIFY_PLOT_FLAG,
    //           TownRank.Action.MODIFY_PLOT_NAME
    //   ));
   //}//
//
    //public static Map<NationRank,List<TownsActionOld>> NATION_RANK_PERMISSIONS = new HashMap<>();
    //static {
    //    NATION_RANK_PERMISSIONS.put( NationRank.NONE, Arrays.asList(
    //            NationRank.Action.NONE
    //    ) );
//
    //    NATION_RANK_PERMISSIONS.put( NationRank.RESIDENT, Arrays.asList(
    //            NationRank.Action.NATION_DEPOSIT,
    //            NationRank.Action.CREATE_NATION
    //    ) );
//
    //    NATION_RANK_PERMISSIONS.put( NationRank.CO_LEADER, Arrays.asList(
    //            NationRank.Action.NATION_DEPOSIT,
    //            NationRank.Action.NATION_WITHDRAW,
    //            NationRank.Action.CREATE_NATION
    //    ) );
//
    //    NATION_RANK_PERMISSIONS.put( NationRank.LEADER, Arrays.asList(
    //            NationRank.Action.NATION_DEPOSIT,
    //            NationRank.Action.NATION_WITHDRAW,
    //            NationRank.Action.SET_COLOR,
    //            NationRank.Action.SET_NAME
    //    ) );
    //}

    TownsConfig( ) throws IOException {
        super( AtherysTowns.getInstance().getWorkingDirectory(), "config.conf" );

        //Asset defaultConfig = AtherysTowns.getInstance().getGame().getAssetManager().getAsset( AtherysTowns.getInstance(), "config.conf").get();
//
        //File configFile = new File("config/" + AtherysTowns.ID, "/config.conf");
        //try {
        //    if ( !configFile.exists() ) {
        //        if ( configFile.mkdirs() && configFile.createNewFile() ) {
        //            defaultConfig.copyToFile(configFile.toPath(), true, true);
        //        } else {
        //            AtherysTowns.getInstance().getLogger().error("Could not create config directories and/or file.");
        //        }
        //    }
        //} catch (IOException e) {
        //    AtherysTowns.getInstance().getLogger().error("Could not create town config.");
        //    e.printStackTrace();
        //}
//
        //Asset wildernessFilter = AtherysTowns.getInstance().getGame().getAssetManager().getAsset( AtherysTowns.getInstance(), "wilderness_filter.json").get();
//
        //File filterFile = new File("config/" + AtherysTowns.ID, "/wilderness_filter.json");
        //try {
        //    if ( !filterFile.exists() ) {
        //        if ( filterFile.mkdirs() && filterFile.createNewFile() ) {
        //            wildernessFilter.copyToFile(filterFile.toPath(), true, true);
        //        } else {
        //            AtherysTowns.getInstance().getLogger().error("Could not create wilderness filter directories and/or file.");
        //        }
        //    }
        //} catch (IOException e) {
        //    AtherysTowns.getInstance().getLogger().error("Could not create wilderness filter file.");
        //    e.printStackTrace();
        //}
//
        //JsonParser parser = new JsonParser();
//
        ////Asset permissions = AtherysTowns.getInstance().getGame().getAssetManager().getAsset( AtherysTowns.getInstance(), "ranks.json").get();
////
        ////File permissionsFile = new File("config/" + AtherysTowns.ID, "/ranks.json");
        ////try {
        ////    if ( !permissionsFile.exists() ) {
        ////        if ( permissionsFile.mkdirs() && permissionsFile.createNewFile() ) {
        ////            permissions.copyToFile(permissionsFile.toPath(), true, true);
        ////        } else {
        ////            AtherysTowns.getInstance().getLogger().error("Could not create ranks directories and/or file.");
        ////        }
        ////    }
        ////} catch (IOException e) {
        ////    AtherysTowns.getInstance().getLogger().error("Could not create ranks file.");
        ////    e.printStackTrace();
        ////}
////
        ////try {
        ////    JsonObject object = parser.parse( new FileReader(permissionsFile) ).getAsJsonObject();
        ////    RankManager.getInstance().fromJson ( object );
        ////} catch (FileNotFoundException | NoSuchElementException e) {
        ////    AtherysTowns.getInstance().getLogger().error("Could not parse ranks json.");
        ////    e.printStackTrace();
        ////}
//
        //try {
        //    WILDERNESS_REGEN_FILTER = parser.parse( new FileReader(filterFile) ).getAsJsonObject();
        //} catch (FileNotFoundException e) {
        //    AtherysTowns.getInstance().getLogger().error("Could not parse wilderness filter json.");
        //    e.printStackTrace();
        //}
//
        //loader = HoconConfigurationLoader.builder().setPath(configFile.toPath()).build();
//
        //try {
        //    root = loader.load();
        //} catch (IOException e) {
        //    e.printStackTrace();
        //}
//
        //// load town rank permissions
        ////TOWN_RANK_PERMISSIONS = new HashMap<>();
        ////for ( TownRank rank : TownRank.values() ) {
        ////    try {
        ////        List<String> perms = root.getNode("town", "rankPerms", rank.name()).getList(TypeToken.of(String.class));
        ////        ArrayList<TownsActionOld> actions = new ArrayList<>();
        ////        for ( String s : perms ) {
        ////            actions.add(TownRank.Action.valueOf(s));
        ////        }
        ////        TOWN_RANK_PERMISSIONS.put(rank, actions);
        ////    } catch (ObjectMappingException e) {
        ////        AtherysTowns.getInstance().getLogger().error("Could not load town rank permissions");
        ////        e.printStackTrace();
        ////    }
        ////}
////
        ////// load nation rank permissions
        ////NATION_RANK_PERMISSIONS = new HashMap<>();
        ////for ( NationRank rank : NationRank.values() ) {
        ////    try {
        ////        List<String> perms = root.getNode("nation", "rankPerms", rank.name()).getList(TypeToken.of(String.class));
        ////        ArrayList<TownsActionOld> actions = new ArrayList<>();
        ////        for ( String s : perms ) {
        ////            actions.add(TownRank.Action.valueOf(s));
        ////        }
        ////        NATION_RANK_PERMISSIONS.put(rank, actions);
        ////    } catch (ObjectMappingException e) {
        ////        AtherysTowns.getInstance().getLogger().error("Could not load nation rank permissions");
        ////        e.printStackTrace();
        ////    }
        ////}
//
        //// load wilderness regen filter
        ////WILDERNESS_REGEN_FILTER = new WildernessManager.WildernessRegenFilter();
        ////for ( ConfigurationNode node : root.getNode("wilderness", "regenFilter").getChildrenList() ) {
        ////    int percent = node.getNode("%").getInt(100);
        ////    String alt =  node.getNode("alt").getString("minecraft:stone");
        ////    String type = (String) node.getKey();
        ////    WILDERNESS_REGEN_FILTER.addItem(type, new WildernessManager.WildernessRegenFilter.RegenData(percent, type) );
        ////}
//
        //// load switch flag blocks
        //SWITCH_FLAG_BLOCKS = new ArrayList<>();
        //try {
        //    SWITCH_FLAG_BLOCKS = root.getList(TypeToken.of(String.class));
        //} catch (ObjectMappingException e) {
        //    AtherysTowns.getInstance().getLogger().error("Could not load switch flag blocks.");
        //    e.printStackTrace();
        //}
    }
}

