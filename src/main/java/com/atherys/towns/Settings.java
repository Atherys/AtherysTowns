package com.atherys.towns;

import com.atherys.towns.permissions.ranks.*;
import com.google.common.reflect.TypeToken;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.asset.Asset;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

public final class Settings {

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
    public static boolean               WILDERNESS_REGEN_ENABLED =      Settings.get(true, "wilderness", "regen", "enabled");
    public static int                   WILDERNESS_REGEN_RATE =         Settings.get(60, "wilderness", "regen", "rate");
    public static TimeUnit              WILDERNESS_REGEN_RATE_UNIT =    TimeUnit.valueOf(Settings.get("SECONDS", "wilderness", "regen", "unit") );
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

    public ConfigurationNode getRoot() {
        return root;
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

    public boolean getBool(boolean defaultValue, String... path) {
        return root.getNode((Object[]) path).getBoolean(defaultValue);
    }

    private static boolean get ( boolean defaultValue, String... path ) { return instance.getBool(defaultValue, path); }

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
                if ( configFile.mkdirs() && configFile.createNewFile() ) {
                    defaultConfig.copyToFile(configFile.toPath(), true, true);
                } else {
                    AtherysTowns.getInstance().getLogger().error("Could not create config directories and/or file.");
                }
            }
        } catch (IOException e) {
            AtherysTowns.getInstance().getLogger().error("Could not create town config.");
            e.printStackTrace();
        }

        Asset wildernessFilter = AtherysTowns.getInstance().getGame().getAssetManager().getAsset( AtherysTowns.getInstance(), "wilderness_filter.json").get();

        File filterFile = new File("config/" + AtherysTowns.ID, "/wilderness_filter.json");
        try {
            if ( !filterFile.exists() ) {
                if ( filterFile.mkdirs() && filterFile.createNewFile() ) {
                    wildernessFilter.copyToFile(filterFile.toPath(), true, true);
                } else {
                    AtherysTowns.getInstance().getLogger().error("Could not create wilderness filter directories and/or file.");
                }
            }
        } catch (IOException e) {
            AtherysTowns.getInstance().getLogger().error("Could not create wilderness filter file.");
            e.printStackTrace();
        }

        JsonParser parser = new JsonParser();

        //Asset permissions = AtherysTowns.getInstance().getGame().getAssetManager().getAsset( AtherysTowns.getInstance(), "ranks.json").get();
//
        //File permissionsFile = new File("config/" + AtherysTowns.ID, "/ranks.json");
        //try {
        //    if ( !permissionsFile.exists() ) {
        //        if ( permissionsFile.mkdirs() && permissionsFile.createNewFile() ) {
        //            permissions.copyToFile(permissionsFile.toPath(), true, true);
        //        } else {
        //            AtherysTowns.getInstance().getLogger().error("Could not create ranks directories and/or file.");
        //        }
        //    }
        //} catch (IOException e) {
        //    AtherysTowns.getInstance().getLogger().error("Could not create ranks file.");
        //    e.printStackTrace();
        //}
//
        //try {
        //    JsonObject object = parser.parse( new FileReader(permissionsFile) ).getAsJsonObject();
        //    RankManager.getInstance().fromJson ( object );
        //} catch (FileNotFoundException | NoSuchElementException e) {
        //    AtherysTowns.getInstance().getLogger().error("Could not parse ranks json.");
        //    e.printStackTrace();
        //}

        try {
            WILDERNESS_REGEN_FILTER = parser.parse( new FileReader(filterFile) ).getAsJsonObject();
        } catch (FileNotFoundException e) {
            AtherysTowns.getInstance().getLogger().error("Could not parse wilderness filter json.");
            e.printStackTrace();
        }

        loader = HoconConfigurationLoader.builder().setPath(configFile.toPath()).build();

        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // load town rank permissions
        //TOWN_RANK_PERMISSIONS = new HashMap<>();
        //for ( TownRank rank : TownRank.values() ) {
        //    try {
        //        List<String> perms = root.getNode("town", "rankPerms", rank.name()).getList(TypeToken.of(String.class));
        //        ArrayList<TownsActionOld> actions = new ArrayList<>();
        //        for ( String s : perms ) {
        //            actions.add(TownRank.Action.valueOf(s));
        //        }
        //        TOWN_RANK_PERMISSIONS.put(rank, actions);
        //    } catch (ObjectMappingException e) {
        //        AtherysTowns.getInstance().getLogger().error("Could not load town rank permissions");
        //        e.printStackTrace();
        //    }
        //}
//
        //// load nation rank permissions
        //NATION_RANK_PERMISSIONS = new HashMap<>();
        //for ( NationRank rank : NationRank.values() ) {
        //    try {
        //        List<String> perms = root.getNode("nation", "rankPerms", rank.name()).getList(TypeToken.of(String.class));
        //        ArrayList<TownsActionOld> actions = new ArrayList<>();
        //        for ( String s : perms ) {
        //            actions.add(TownRank.Action.valueOf(s));
        //        }
        //        NATION_RANK_PERMISSIONS.put(rank, actions);
        //    } catch (ObjectMappingException e) {
        //        AtherysTowns.getInstance().getLogger().error("Could not load nation rank permissions");
        //        e.printStackTrace();
        //    }
        //}

        // load wilderness regen filter
        //WILDERNESS_REGEN_FILTER = new WildernessManager.WildernessRegenFilter();
        //for ( ConfigurationNode node : root.getNode("wilderness", "regenFilter").getChildrenList() ) {
        //    int percent = node.getNode("%").getInt(100);
        //    String alt =  node.getNode("alt").getString("minecraft:stone");
        //    String type = (String) node.getKey();
        //    WILDERNESS_REGEN_FILTER.addItem(type, new WildernessManager.WildernessRegenFilter.RegenData(percent, type) );
        //}

        // load switch flag blocks
        SWITCH_FLAG_BLOCKS = new ArrayList<>();
        try {
            SWITCH_FLAG_BLOCKS = root.getList(TypeToken.of(String.class));
        } catch (ObjectMappingException e) {
            AtherysTowns.getInstance().getLogger().error("Could not load switch flag blocks.");
            e.printStackTrace();
        }
    }

    public static Settings getInstance() {
        return instance;
    }
}

