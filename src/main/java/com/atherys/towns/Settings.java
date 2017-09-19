package com.atherys.towns;

import com.atherys.towns.managers.WildernessManager;
import com.atherys.towns.resident.ranks.NationRank;
import com.atherys.towns.resident.ranks.TownRank;
import com.atherys.towns.resident.ranks.TownsAction;
import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.asset.Asset;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

public final class Settings {
//
//    private static JsonParser parser = new JsonParser();
//    private static ConfigurationLoader<ConfigurationNode> loader;
//    private static ConfigurationNode root;
//
//    enum SettingNode {
//
//        DECORATION_COLOR            ( "GOLD",           "towns", "colors",  "decoration"),
//        PRIMARY_COLOR               ( "DARK_GREEN",     "towns", "colors",  "primary"),
//        SECONDARY_COLOR             ( "GREEN",          "towns", "colors",  "secondary"),
//        TERTIARY_COLOR              ( "AQUA",           "towns", "colors",  "tertiary"),
//        TEXT_COLOR                  ( "YELLOW",         "towns", "colors",  "text"),
//        WARNING_COLOR               ( "RED",            "towns", "colors",  "warning"),
//        TOWN_BORDER_UPDATE_SECONDS  ( 4,                "towns", "border",  "updateRate"),
//        NPC_NAME                    ( "NPC",            "towns", "npc",     "name" ),
//        MAX_TOWN_NAME_LENGTH        ( 24,               "towns", "town",    "maxNameLength"),
//        MAX_PLOT_AREA               ( 512,              "towns", "plots",   "maxArea"),
//        MIN_SIZE_PLOT_SIDE          ( 16,               "towns", "plots",   "minSideSize"),
//        INITIAL_TOWN_AREA_LIMIT     ( 5120,             "towns", "plots",   "initialTownArea"),
//        TOWN_TITLE_FADEIN_TICKS     ( 5,                "towns", "titles",  "title", "fadeinTicks"),
//        TOWN_TITLE_STAY_TICKS       ( 10,               "towns", "titles",  "title", "stayTicks"),
//        TOWN_TITLE_FADEOUT_TICKS    ( 5,                "towns", "titles",  "title", "fadeoutTicks"),
//        TOWN_SUBTITLE_FADEIN_TICKS  ( 5,                "towns", "titles",  "subtitle", "fadeinTicks"),
//        TOWN_SUBTITLE_STAY_TICKS    ( 10,               "towns", "titles",  "subtitle", "stayTicks"),
//        TOWN_SUBTITLE_FADEOUT_TICKS ( 5,                "towns", "titles",  "subtitle", "fadeoutTicks"),
//        SQL_TYPE                    ( "SQLITE",         "towns", "database","type"),
//        SQL_HOST                    ( "127.0.0.1",      "towns", "database","host"),
//        SQL_PORT                    ( 3306,             "towns", "database","port"),
//        SQL_DATABASE                ( "towns_Database", "towns", "database","dbName"),
//        SQL_USER                    ( "user",           "towns", "database","user"),
//        SQL_PASSWORD                ( "password",       "towns", "database","password"),
//        TOWN_SPAWN_DELAY            ( 10L,              "towns", "spawn",   "delay"),
//        WILDERNESS_REGEN_RATE       ( 60L,              "towns", "wilderness","regenRate"),
//        WILDERNESS_REGEN_RATE_UNIT  ( "SECONDS",        "towns", "wilderness","regenUnit"),
//        WILDERNESS_REGEN_FILTER     ( "{\n" +
//                "        \"minecraft:stone\":{\"%\":\"100\",\"alt\":\"minecraft:stone\"},\n" +
//                "        \"minecraft:grass\":{\"%\":\"100\",\"alt\":\"minecraft:stone\"},\n" +
//                "        \"minecraft:dirt\":{\"%\":\"100\",\"alt\":\"minecraft:stone\"},\n" +
//                "        \"minecraft:cobblestone\":{\"%\":\"100\",\"alt\":\"minecraft:stone\"},\n" +
//                "        \"minecraft:iron_ore\":{\"%\":\"50\",\"alt\":\"minecraft:stone\"}\n" +
//                "      }",               "towns", "wilderness", "regenFilter"),
//        SWITCH_FLAG_BLOCKS          ("[\"minecraft:chest\",\"minecraft:furnace\"]",
//                                         "towns", "flags", "switchBlocks");
//
//        Object[] nodePath;
//        Object defaultValue;
//
//        SettingNode ( Object defaultValue, String... path) {
//            this.nodePath = path;
//            this.defaultValue = defaultValue;
//        }
//
//        void set ( Object newValue ) {
//            if ( newValue.getClass().equals(defaultValue.getClass()) ) {
//                root.getNode(nodePath).setValue(newValue);
//            }
//        }
//
//        boolean getBool() {
//            return root.getNode(nodePath).getBoolean((Boolean) defaultValue);
//        }
//
//        String getString() {
//            return root.getNode(nodePath).getString((String) defaultValue);
//        }
//
//        int getInt() {
//            return root.getNode(nodePath).getInt((Integer) defaultValue);
//        }
//
//        long getLong() {
//            return root.getNode(nodePath).getLong((Long) defaultValue);
//        }
//
//        float getFloat() {
//            return root.getNode(nodePath).getFloat((Float) defaultValue);
//        }
//
//        double getDouble() {
//            return root.getNode(nodePath).getDouble((Double) defaultValue);
//        }
//    }
//
//    public static TextColor DECORATION_COLOR;
//    public static TextColor PRIMARY_COLOR;
//    public static TextColor SECONDARY_COLOR;
//    public static TextColor TERTIARY_COLOR;
//    public static TextColor TEXT_COLOR;
//    public static TextColor WARNING_COLOR;
//
//    public static TextColor TOWN_CHAT_COLOR;
//    public static int TOWN_BORDER_UPDATE_SECONDS;
//
//    public static String NON_PLAYER_CHARACTER_NAME;
//
//    public static int MAX_TOWN_NAME_LENGTH;
//
//    public static int MAX_PLOT_AREA;
//    public static int MIN_SIZE_PLOT_SIDE;
//    public static int INITIAL_TOWN_AREA_LIMIT;
//
//    public static int TOWN_TITLE_FADEIN_TICKS;
//    public static int TOWN_TITLE_STAY_TICKS;
//    public static int TOWN_TITLE_FADEOUT_TICKS;
//
//    public static int TOWN_SUBTITLE_FADEIN_TICKS;
//    public static int TOWN_SUBTITLE_STAY_TICKS;
//    public static int TOWN_SUBTITLE_FADEOUT_TICKS;
//
//    public static List<String> SWITCH_FLAG_BLOCKS;
//
//    public static TownsDatabase.Type SQL_TYPE;
//    public static String             SQL_HOST;
//    public static int                SQL_PORT;
//    public static String             SQL_DATABASE;
//    public static String             SQL_USER;
//    public static String             SQL_PASSWORD;
//
//    public static PlotFlags.Extent[] PVP_FLAG_ALLOWED_EXTENTS = {PlotFlags.Extent.NONE, PlotFlags.Extent.ALL};
//    public static PlotFlags.Extent[] BUILD_FLAG_ALLOWED_EXTENTS = {PlotFlags.Extent.NONE, PlotFlags.Extent.ALL, PlotFlags.Extent.TOWN, PlotFlags.Extent.NATION, PlotFlags.Extent.ALLIES, PlotFlags.Extent.NEUTRALS, PlotFlags.Extent.ENEMIES};
//    public static PlotFlags.Extent[] DESTROY_FLAG_ALLOWED_EXTENTS = {PlotFlags.Extent.NONE, PlotFlags.Extent.ALL, PlotFlags.Extent.TOWN, PlotFlags.Extent.NATION, PlotFlags.Extent.ALLIES, PlotFlags.Extent.NEUTRALS, PlotFlags.Extent.ENEMIES};
//    public static PlotFlags.Extent[] SWITCH_FLAG_ALLOWED_EXTENTS = {PlotFlags.Extent.NONE, PlotFlags.Extent.ALL, PlotFlags.Extent.TOWN, PlotFlags.Extent.NATION, PlotFlags.Extent.ALLIES, PlotFlags.Extent.NEUTRALS, PlotFlags.Extent.ENEMIES};
//    public static PlotFlags.Extent[] DAMAGE_ENTITY_FLAG_ALLOWED_EXTENTS = {PlotFlags.Extent.NONE, PlotFlags.Extent.ALL, PlotFlags.Extent.TOWN, PlotFlags.Extent.NATION, PlotFlags.Extent.ALLIES, PlotFlags.Extent.NEUTRALS, PlotFlags.Extent.ENEMIES};
//    public static PlotFlags.Extent[] JOIN_ALLOWED_EXTENTS = {PlotFlags.Extent.NONE, PlotFlags.Extent.ALL};
//
//    public static long TOWN_SPAWN_DELAY;
//
//    public static long WILDERNESS_REGEN_RATE;
//    public static TimeUnit WILDERNESS_REGEN_RATE_UNIT;
//    public static Map<String,Tuple<Double,String>> WILDERNESS_REGEN_FILTER;
//    public static Double DEFAULT_REGEN_RATE = 100.0d;
//    public static String DEFAULT_REGEN_MATERIAL = "minecraft:stone";
//
//    private static String serializeRegenFilter () {
//        JsonObject obj = new JsonObject();
//
//        WILDERNESS_REGEN_FILTER.forEach( (k,v) -> {
//            JsonObject tuple = new JsonObject();
//            tuple.addProperty("%", v.getFirst());
//            tuple.addProperty("alt", v.getSecond());
//            obj.add(k, tuple);
//        });
//
//        return obj.toString();
//    }
//
//    private static Map<String,Tuple<Double,String>> deserializeRegenFilter() {
//        Map<String,Tuple<Double,String>> filter = new HashMap<>();
//        JsonElement obj = parser.parse(SettingNode.WILDERNESS_REGEN_FILTER.getString());
//        if ( obj.isJsonNull() ) return filter;
//        obj.getAsJsonObject().entrySet().forEach( (k) -> {
//            JsonObject tuple = k.getValue().getAsJsonObject();
//            if ( tuple.isJsonNull() ) return;
//            filter.put(k.getKey(), Tuple.of( tuple.get("%").getAsDouble(), tuple.get("alt").getAsString() ) );
//        });
//
//        return filter;
//    }
//
//    private static List<String> deserializeSwitchBlocks() {
//        List<String> list = new LinkedList<>();
//        JsonElement obj = parser.parse(SettingNode.SWITCH_FLAG_BLOCKS.getString());
//        if ( obj.isJsonNull() || !obj.isJsonArray() ) return list;
//        JsonArray arr = obj.getAsJsonArray();
//        for ( JsonElement e : arr ) {
//            list.add(e.getAsString());
//        }
//        return list;
//    }
//
//    /*
//    * "wildernessRegenFilter":
//    * {
//    *   "minecraft:stone":{ "%":"100", "alt":"minecraft:stone" },
//    *   "minecraft:grass":{ "%":"100", "alt":"minecraft:stone" },
//    *   "minecraft:dirt":{ "%":"100", "alt":"minecraft:stone" },
//    *   "minecraft:cobblestone":{ "%":"100", "alt":"minecraft:stone" },
//    *   "minecraft:iron_ore":{ "%":"50", "alt":"minecraft:stone" },
//    * }
//    *
//    *
//    * */
//
//    public static Map<TownRank,List<TownsAction>> TOWN_RANK_PERMISSIONS = new HashMap<>();
//    static {
//        TOWN_RANK_PERMISSIONS.put(TownRank.NONE, Arrays.asList(
//                TownRank.Action.NULL,
//                TownRank.Action.CHAT,
//                TownRank.Action.JOIN_TOWN,
//                TownRank.Action.LEAVE_TOWN
//        ));
//        TOWN_RANK_PERMISSIONS.put(TownRank.RESIDENT, Arrays.asList(
//                TownRank.Action.NULL,
//                TownRank.Action.CHAT,
//                TownRank.Action.JOIN_TOWN,
//                TownRank.Action.LEAVE_TOWN,
//                TownRank.Action.TOWN_DEPOSIT,
//                TownRank.Action.NATION_DEPOSIT
//        ));
//        TOWN_RANK_PERMISSIONS.put(TownRank.CITIZEN, Arrays.asList(
//                TownRank.Action.NULL,
//                TownRank.Action.CHAT,
//                TownRank.Action.JOIN_TOWN,
//                TownRank.Action.LEAVE_TOWN,
//                TownRank.Action.TOWN_DEPOSIT,
//                TownRank.Action.NATION_DEPOSIT
//        ));
//        TOWN_RANK_PERMISSIONS.put(TownRank.ASSISTANT, Arrays.asList(
//                TownRank.Action.NULL,
//                TownRank.Action.CHAT,
//                TownRank.Action.JOIN_TOWN,
//                TownRank.Action.LEAVE_TOWN,
//                TownRank.Action.INVITE_PLAYER,
//                TownRank.Action.KICK_PLAYER,
//                TownRank.Action.SET_MOTD,
//                TownRank.Action.SET_DESCRIPTION,
//                TownRank.Action.SET_COLOR,
//                TownRank.Action.SHOW_TOWN_BORDER,
//                TownRank.Action.TOWN_DEPOSIT,
//                TownRank.Action.NATION_DEPOSIT
//        ));
//        TOWN_RANK_PERMISSIONS.put(TownRank.CO_MAYOR, Arrays.asList(
//                TownRank.Action.NULL,
//                TownRank.Action.CHAT,
//                TownRank.Action.INVITE_PLAYER,
//                TownRank.Action.KICK_PLAYER,
//                TownRank.Action.LEAVE_TOWN,
//                TownRank.Action.SET_MOTD,
//                TownRank.Action.SET_DESCRIPTION,
//                TownRank.Action.SET_COLOR,
//                TownRank.Action.SET_NAME,
//                TownRank.Action.SET_RANK,
//                TownRank.Action.CLAIM_PLOT,
//                TownRank.Action.UNCLAIM_PLOT,
//                TownRank.Action.SET_FLAGS,
//                TownRank.Action.SET_FLAG_PVP,
//                TownRank.Action.SHOW_TOWN_BORDER,
//                TownRank.Action.TOWN_DEPOSIT,
//                TownRank.Action.TOWN_WITHDRAW,
//                TownRank.Action.NATION_DEPOSIT,
//                TownRank.Action.MODIFY_PLOT_FLAG,
//                TownRank.Action.MODIFY_PLOT_NAME
//        ));
//        TOWN_RANK_PERMISSIONS.put(TownRank.MAYOR, Arrays.asList(
//                TownRank.Action.NULL,
//                TownRank.Action.CHAT,
//                TownRank.Action.INVITE_PLAYER,
//                TownRank.Action.KICK_PLAYER,
//                TownRank.Action.SET_MOTD,
//                TownRank.Action.SET_DESCRIPTION,
//                TownRank.Action.SET_COLOR,
//                TownRank.Action.CLAIM_PLOT,
//                TownRank.Action.UNCLAIM_PLOT,
//                TownRank.Action.SET_NAME,
//                TownRank.Action.SET_RANK,
//                TownRank.Action.SET_MAYOR,
//                TownRank.Action.SET_FLAGS,
//                TownRank.Action.SET_FLAG_PVP,
//                TownRank.Action.SET_FLAG_BUILD,
//                TownRank.Action.SET_FLAG_DESTROY,
//                TownRank.Action.SET_FLAG_JOIN,
//                TownRank.Action.SET_FLAG_SWITCH,
//                TownRank.Action.SET_FLAG_DAMAGE_ENTITY,
//                TownRank.Action.RUIN_TOWN,
//                TownRank.Action.SHOW_TOWN_BORDER,
//                TownRank.Action.TOWN_DEPOSIT,
//                TownRank.Action.TOWN_WITHDRAW,
//                TownRank.Action.NATION_DEPOSIT,
//                TownRank.Action.MODIFY_PLOT_FLAG,
//                TownRank.Action.MODIFY_PLOT_NAME
//        ));
//    }
//
//    public static Map<NationRank,List<TownsAction>> NATION_RANK_PERMISSIONS = new HashMap<>();
//    static {
//        NATION_RANK_PERMISSIONS.put( NationRank.NONE, Arrays.asList(
//                NationRank.Action.NONE
//        ) );
//
//        NATION_RANK_PERMISSIONS.put( NationRank.RESIDENT, Arrays.asList(
//                NationRank.Action.NATION_DEPOSIT,
//                NationRank.Action.CREATE_NATION
//        ) );
//
//        NATION_RANK_PERMISSIONS.put( NationRank.CO_LEADER, Arrays.asList(
//                NationRank.Action.NATION_DEPOSIT,
//                NationRank.Action.NATION_WITHDRAW,
//                NationRank.Action.CREATE_NATION
//        ) );
//
//        NATION_RANK_PERMISSIONS.put( NationRank.LEADER, Arrays.asList(
//                NationRank.Action.NATION_DEPOSIT,
//                NationRank.Action.NATION_WITHDRAW,
//                NationRank.Action.SET_COLOR,
//                NationRank.Action.SET_NAME
//        ) );
//    }
//
//    static void load () throws IOException {
//
//        Plugin annotation = AtherysTowns.getInstance().getClass().getAnnotation(Plugin.class);
//
//        File configFile = new File("config/" + annotation.id(), "config.yml");
//        boolean file = configFile.exists();
//        if ( !file ) {
//            configFile.getParentFile().mkdirs();
//            configFile.createNewFile();
//        }
//
//        loader = YAMLConfigurationLoader.builder().setPath(configFile.toPath()).build();
//        root = loader.load();
//
//        if ( !file ) {
//            saveDefaults();
//        }
//
//        DECORATION_COLOR =              AtherysTowns.getInstance().getGame().getRegistry().getType(TextColor.class, SettingNode.DECORATION_COLOR.getString()).orElse(TextColors.GOLD);
//        PRIMARY_COLOR =                 AtherysTowns.getInstance().getGame().getRegistry().getType(TextColor.class, SettingNode.PRIMARY_COLOR.getString()).orElse(TextColors.DARK_GREEN);
//        SECONDARY_COLOR =               AtherysTowns.getInstance().getGame().getRegistry().getType(TextColor.class, SettingNode.SECONDARY_COLOR.getString()).orElse(TextColors.GREEN);
//        TERTIARY_COLOR =                AtherysTowns.getInstance().getGame().getRegistry().getType(TextColor.class, SettingNode.TERTIARY_COLOR.getString()).orElse(TextColors.AQUA);
//        TEXT_COLOR =                    AtherysTowns.getInstance().getGame().getRegistry().getType(TextColor.class, SettingNode.TEXT_COLOR.getString()).orElse(TextColors.YELLOW);
//        WARNING_COLOR =                 AtherysTowns.getInstance().getGame().getRegistry().getType(TextColor.class, SettingNode.WARNING_COLOR.getString()).orElse(TextColors.RED);
//        TOWN_CHAT_COLOR =               TextColors.DARK_AQUA;
//        TOWN_BORDER_UPDATE_SECONDS =    SettingNode.TOWN_BORDER_UPDATE_SECONDS.getInt();
//        NON_PLAYER_CHARACTER_NAME =     SettingNode.NPC_NAME.getString();
//        MAX_TOWN_NAME_LENGTH =          SettingNode.MAX_TOWN_NAME_LENGTH.getInt();
//        MAX_PLOT_AREA =                 SettingNode.MAX_PLOT_AREA.getInt();
//        MIN_SIZE_PLOT_SIDE =            SettingNode.MIN_SIZE_PLOT_SIDE.getInt();
//        INITIAL_TOWN_AREA_LIMIT =       SettingNode.INITIAL_TOWN_AREA_LIMIT.getInt();
//        TOWN_TITLE_FADEIN_TICKS =       SettingNode.TOWN_TITLE_FADEIN_TICKS.getInt();
//        TOWN_TITLE_STAY_TICKS =         SettingNode.TOWN_TITLE_STAY_TICKS.getInt();
//        TOWN_TITLE_FADEOUT_TICKS =      SettingNode.TOWN_TITLE_FADEOUT_TICKS.getInt();
//        TOWN_SUBTITLE_FADEIN_TICKS =    SettingNode.TOWN_SUBTITLE_FADEIN_TICKS.getInt();
//        TOWN_SUBTITLE_STAY_TICKS =      SettingNode.TOWN_SUBTITLE_STAY_TICKS.getInt();
//        TOWN_SUBTITLE_FADEOUT_TICKS =   SettingNode.TOWN_SUBTITLE_FADEOUT_TICKS.getInt();
//        SQL_TYPE =                      TownsDatabase.Type.valueOf(SettingNode.SQL_TYPE.getString());
//        SQL_HOST =                      SettingNode.SQL_HOST.getString();
//        SQL_PORT =                      SettingNode.SQL_PORT.getInt();
//        SQL_DATABASE =                  SettingNode.SQL_DATABASE.getString();
//        SQL_USER =                      SettingNode.SQL_USER.getString();
//        SQL_PASSWORD =                  SettingNode.SQL_PASSWORD.getString();
//        TOWN_SPAWN_DELAY =              SettingNode.TOWN_SPAWN_DELAY.getLong();
//        WILDERNESS_REGEN_RATE =         SettingNode.WILDERNESS_REGEN_RATE.getLong();
//        WILDERNESS_REGEN_RATE_UNIT =    TimeUnit.valueOf(SettingNode.WILDERNESS_REGEN_RATE_UNIT.getString());
//        WILDERNESS_REGEN_FILTER =       deserializeRegenFilter();
//        SWITCH_FLAG_BLOCKS =            deserializeSwitchBlocks();
//    }
//
//    static void saveDefaults () throws IOException {
//        for ( SettingNode setting : SettingNode.values() ) {
//            root.getNode(setting.nodePath).setValue(setting.defaultValue);
//        }
//        loader.save(root);
//    }




    private static final Settings instance = new Settings();

    private HoconConfigurationLoader loader;
    private ConfigurationNode root;

    public static TextColor             DECORATION_COLOR =              Settings.get(TextColors.GOLD, "colors", "decoration");
    public static TextColor             PRIMARY_COLOR =                 Settings.get(TextColors.DARK_GREEN, "colors", "primary");
    public static TextColor             SECONDARY_COLOR =               Settings.get(TextColors.GREEN, "colors", "secondary");
    public static TextColor             TERTIARY_COLOR =                Settings.get(TextColors.AQUA, "colors", "tertiary");
    public static TextColor             TEXT_COLOR =                    Settings.get(TextColors.YELLOW, "colors", "text");
    public static TextColor             WARNING_COLOR =                 Settings.get(TextColors.RED, "colors", "warn");
    public static TextColor             TOWN_CHAT_COLOR =               Settings.get(TextColors.DARK_AQUA, "colors", "chat");
    public static int                   TOWN_BORDER_UPDATE_RATE =       Settings.get(4, "border", "updateRate");
    public static String                NON_PLAYER_CHARACTER_NAME =     Settings.get("NPC", "npc", "name");
    public static int                   MAX_TOWN_NAME_LENGTH =          Settings.get(4, "town", "maxNameLength");
    public static int                   MAX_PLOT_AREA =                 Settings.get(512, "plots", "maxArea");
    public static int                   MIN_SIZE_PLOT_SIDE =            Settings.get(16, "plots", "minSideSize");
    public static int                   INITIAL_TOWN_AREA_LIMIT =       Settings.get(5120, "plots", "initialTownArea");
    public static int                   TITLE_FADEIN_TICKS =            Settings.get(5, "titles", "title", "fadeInTicks");
    public static int                   TITLE_STAY_TICKS =              Settings.get(10, "titles", "title", "stayTicks");
    public static int                   TITLE_FADEOUT_TICKS =           Settings.get(5, "titles", "title", "fadeoutTicks");
    public static int                   SUBTITLE_FADEIN_TICKS =         Settings.get(5, "titles", "subtitle", "fadeInTicks");
    public static int                   SUBTITLE_STAY_TICKS =           Settings.get(10, "titles", "subtitle", "stayTicks");
    public static int                   SUBTITLE_FADEOUT_TICKS =        Settings.get(5, "titles", "subtitle", "fadeoutTicks");
    public static String                DB_HOST =                       Settings.get("localhost", "database", "host");
    public static int                   DB_PORT =                       Settings.get(27017, "database", "port");
    public static String                DB_DATABASE =                   Settings.get("towns_database", "database", "dbName");
    public static String                DB_USER_DB =                    Settings.get("admin", "database", "userDb");
    public static String                DB_USER =                       Settings.get("user", "database", "user");
    public static String                DB_PASSWORD =                   Settings.get("password", "database", "password");
    public static int                   TOWN_SPAWN_DELAY =              Settings.get(10, "spawn", "delay");
    public static int                   WILDERNESS_REGEN_RATE =         Settings.get(60, "wilderness", "regenRate");
    public static TimeUnit              WILDERNESS_REGEN_RATE_UNIT =    TimeUnit.valueOf(Settings.get("SECONDS", "wilderness", "regenUnit") );
    public static WildernessManager.WildernessRegenFilter WILDERNESS_REGEN_FILTER = new WildernessManager.WildernessRegenFilter();
    public static List<String>                SWITCH_FLAG_BLOCKS =      new ArrayList<>();

    public static Map<TownRank,List<TownsAction>>   TOWN_RANK_PERMISSIONS = new HashMap<>();
    static {
       TOWN_RANK_PERMISSIONS.put(TownRank.NONE, Arrays.asList(
               TownRank.Action.NULL,
               TownRank.Action.CHAT,
               TownRank.Action.JOIN_TOWN,
               TownRank.Action.LEAVE_TOWN
       ));
       TOWN_RANK_PERMISSIONS.put(TownRank.RESIDENT, Arrays.asList(
               TownRank.Action.NULL,
               TownRank.Action.CHAT,
               TownRank.Action.JOIN_TOWN,
               TownRank.Action.LEAVE_TOWN,
               TownRank.Action.TOWN_DEPOSIT,
               TownRank.Action.NATION_DEPOSIT
       ));
       TOWN_RANK_PERMISSIONS.put(TownRank.CITIZEN, Arrays.asList(
               TownRank.Action.NULL,
               TownRank.Action.CHAT,
               TownRank.Action.JOIN_TOWN,
               TownRank.Action.LEAVE_TOWN,
               TownRank.Action.TOWN_DEPOSIT,
               TownRank.Action.NATION_DEPOSIT
       ));
       TOWN_RANK_PERMISSIONS.put(TownRank.ASSISTANT, Arrays.asList(
               TownRank.Action.NULL,
               TownRank.Action.CHAT,
               TownRank.Action.JOIN_TOWN,
               TownRank.Action.LEAVE_TOWN,
               TownRank.Action.INVITE_PLAYER,
               TownRank.Action.KICK_PLAYER,
               TownRank.Action.SET_MOTD,
               TownRank.Action.SET_DESCRIPTION,
               TownRank.Action.SET_COLOR,
               TownRank.Action.SHOW_TOWN_BORDER,
               TownRank.Action.TOWN_DEPOSIT,
               TownRank.Action.NATION_DEPOSIT
       ));
       TOWN_RANK_PERMISSIONS.put(TownRank.CO_MAYOR, Arrays.asList(
               TownRank.Action.NULL,
               TownRank.Action.CHAT,
               TownRank.Action.INVITE_PLAYER,
               TownRank.Action.KICK_PLAYER,
               TownRank.Action.LEAVE_TOWN,
               TownRank.Action.SET_MOTD,
               TownRank.Action.SET_DESCRIPTION,
               TownRank.Action.SET_COLOR,
               TownRank.Action.SET_NAME,
               TownRank.Action.SET_RANK,
               TownRank.Action.CLAIM_PLOT,
               TownRank.Action.UNCLAIM_PLOT,
               TownRank.Action.SET_FLAGS,
               TownRank.Action.SET_FLAG_PVP,
               TownRank.Action.SHOW_TOWN_BORDER,
               TownRank.Action.TOWN_DEPOSIT,
               TownRank.Action.TOWN_WITHDRAW,
               TownRank.Action.NATION_DEPOSIT,
               TownRank.Action.MODIFY_PLOT_FLAG,
               TownRank.Action.MODIFY_PLOT_NAME
       ));
       TOWN_RANK_PERMISSIONS.put(TownRank.MAYOR, Arrays.asList(
               TownRank.Action.NULL,
               TownRank.Action.CHAT,
               TownRank.Action.INVITE_PLAYER,
               TownRank.Action.KICK_PLAYER,
               TownRank.Action.SET_MOTD,
               TownRank.Action.SET_DESCRIPTION,
               TownRank.Action.SET_COLOR,
               TownRank.Action.CLAIM_PLOT,
               TownRank.Action.UNCLAIM_PLOT,
               TownRank.Action.SET_NAME,
               TownRank.Action.SET_RANK,
               TownRank.Action.SET_MAYOR,
               TownRank.Action.SET_FLAGS,
               TownRank.Action.SET_FLAG_PVP,
               TownRank.Action.SET_FLAG_BUILD,
               TownRank.Action.SET_FLAG_DESTROY,
               TownRank.Action.SET_FLAG_JOIN,
               TownRank.Action.SET_FLAG_SWITCH,
               TownRank.Action.SET_FLAG_DAMAGE_ENTITY,
               TownRank.Action.RUIN_TOWN,
               TownRank.Action.SHOW_TOWN_BORDER,
               TownRank.Action.TOWN_DEPOSIT,
               TownRank.Action.TOWN_WITHDRAW,
               TownRank.Action.NATION_DEPOSIT,
               TownRank.Action.MODIFY_PLOT_FLAG,
               TownRank.Action.MODIFY_PLOT_NAME
       ));
   }

    public static Map<NationRank,List<TownsAction>> NATION_RANK_PERMISSIONS = new HashMap<>();
    static {
        NATION_RANK_PERMISSIONS.put( NationRank.NONE, Arrays.asList(
                NationRank.Action.NONE
        ) );

        NATION_RANK_PERMISSIONS.put( NationRank.RESIDENT, Arrays.asList(
                NationRank.Action.NATION_DEPOSIT,
                NationRank.Action.CREATE_NATION
        ) );

        NATION_RANK_PERMISSIONS.put( NationRank.CO_LEADER, Arrays.asList(
                NationRank.Action.NATION_DEPOSIT,
                NationRank.Action.NATION_WITHDRAW,
                NationRank.Action.CREATE_NATION
        ) );

        NATION_RANK_PERMISSIONS.put( NationRank.LEADER, Arrays.asList(
                NationRank.Action.NATION_DEPOSIT,
                NationRank.Action.NATION_WITHDRAW,
                NationRank.Action.SET_COLOR,
                NationRank.Action.SET_NAME
        ) );
    }

    public Optional<Object> getValue (String... path ) {
        return Optional.ofNullable(root.getNode((Object[]) path).getValue());
    }

    public void setValue ( Object obj, String... path ) {
        root.getNode((Object[]) path).setValue(obj);
    }

    public int getInt( int defaultValue, String... path ) {
        return root.getNode((Object[]) path).getInt(defaultValue);
    }

    public Double getDouble ( double defaultValue, String... path ) {
        return root.getNode((Object[]) path).getDouble(defaultValue);
    }

    public void setDouble ( double value, String... path ) {
        root.getNode((Object[]) path).setValue(value);
    }

    public String getString( String defaultValue, String... path) {
        return root.getNode((Object[]) path).getString(defaultValue);
    }

    public static Settings getInstance() {
        return instance;
    }

    private static int get ( int defaultValue, String... path ) {
        return instance.getInt(defaultValue, path);
    }

    private static double get ( double defaultValue, String... path ) {
        return instance.getDouble(defaultValue, path);
    }

    private static String get ( String defaultValue, String... path ) {
        return instance.getString(defaultValue, path);
    }

    private static TextColor get ( TextColor defaultValue, String... path ) {
        return Sponge.getRegistry().getType( TextColor.class, get( defaultValue.getId(), path ) ).orElse( defaultValue );
    }

    private Settings( ) {

        Asset defaultConfig = AtherysTowns.getInstance().getGame().getAssetManager().getAsset( AtherysTowns.getInstance(), "config.conf").get();

        File configFile = new File("config/" + AtherysTowns.ID, "/config.conf");
        try {
            if ( !configFile.exists() ) {
                defaultConfig.copyToFile(configFile.toPath(), true, true);
            }
        } catch (IOException e) {
            AtherysTowns.getInstance().getLogger().error("Could not create town config.");
            e.printStackTrace();
        }

        loader = HoconConfigurationLoader.builder().setPath(configFile.toPath()).build();

        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // load town rank permissions
        TOWN_RANK_PERMISSIONS = new HashMap<>();
        for ( TownRank rank : TownRank.values() ) {
            try {
                List<String> perms = root.getNode("town", "rankPerms", rank.name()).getList(TypeToken.of(String.class));
                ArrayList<TownsAction> actions = new ArrayList<>();
                for ( String s : perms ) {
                    actions.add(TownRank.Action.valueOf(s));
                }
                TOWN_RANK_PERMISSIONS.put(rank, actions);
            } catch (ObjectMappingException e) {
                AtherysTowns.getInstance().getLogger().error("Could not load town rank permissions");
                e.printStackTrace();
            }
        }

        // load nation rank permissions
        NATION_RANK_PERMISSIONS = new HashMap<>();
        for ( NationRank rank : NationRank.values() ) {
            try {
                List<String> perms = root.getNode("nation", "rankPerms", rank.name()).getList(TypeToken.of(String.class));
                ArrayList<TownsAction> actions = new ArrayList<>();
                for ( String s : perms ) {
                    actions.add(TownRank.Action.valueOf(s));
                }
                NATION_RANK_PERMISSIONS.put(rank, actions);
            } catch (ObjectMappingException e) {
                AtherysTowns.getInstance().getLogger().error("Could not load nation rank permissions");
                e.printStackTrace();
            }
        }

        // load wilderness regen filter
        WILDERNESS_REGEN_FILTER = new WildernessManager.WildernessRegenFilter();
        for ( ConfigurationNode node : root.getNode("wilderness", "regenFilter").getChildrenList() ) {
            int percent = node.getNode("%").getInt(100);
            String alt =  node.getNode("alt").getString("minecraft:stone");
            String type = (String) node.getKey();
            WILDERNESS_REGEN_FILTER.addItem(type, new WildernessManager.WildernessRegenFilter.RegenData(percent, type) );
        }

        // load switch flag blocks
        SWITCH_FLAG_BLOCKS = new ArrayList<>();
        try {
            SWITCH_FLAG_BLOCKS = root.getList(TypeToken.of(String.class));
        } catch (ObjectMappingException e) {
            AtherysTowns.getInstance().getLogger().error("Could not load switch flag blocks.");
            e.printStackTrace();
        }
    }
}

