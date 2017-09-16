package com.atherys.towns.managers;

import com.atherys.towns.AtherysTowns;
import com.atherys.towns.nation.Nation;
import com.atherys.towns.plot.PlotFlags;
import com.atherys.towns.town.Town;
import com.atherys.towns.town.TownStatus;
import com.atherys.towns.utils.DatabaseUtils;
import com.atherys.towns.utils.Deserialize;
import org.spongepowered.api.world.Location;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public final class TownManager extends AreaObjectManager<Town> {

    private static final String CREATE_TABLE_TOWNS =
            "CREATE TABLE IF NOT EXISTS `towns_Towns` (" +
                    "  `uuid` VARCHAR NOT NULL," +
                    "  `status` INTEGER NOT NULL," +
                    "  `nation_uuid` VARCHAR," +
                    "  `flags` VARCHAR NOT NULL," +
                    "  `maxArea` INTEGER NOT NULL," +
                    "  `spawn` VARCHAR NOT NULL," +
                    "  `name` VARCHAR NOT NULL," +
                    "  `color` INTEGER NOT NULL," +
                    "  `motd` VARCHAR NOT NULL," +
                    "  `description` VARCHAR NOT NULL," +
                    "  PRIMARY KEY (`uuid`)" +
                    ");";

    public enum Table implements DatabaseUtils.AbstractTable<Table> {
        UUID        (1,     "uuid",         "VARCHAR(36) NOT NULL"),
        STATUS      (2,     "status",       "INTEGER NOT NULL"),
        NATION_UUID (3,     "nation_uuid",  "VARCHAR(36)"         ),
        FLAGS       (4,     "flags",        "VARCHAR(256) NOT NULL"),
        MAX_AREA    (5,     "maxArea",      "INTEGER NOT NULL"),
        SPAWN       (6,     "spawn",        "VARCHAR(128) NOT NULL"),
        NAME        (7,     "name",         "VARCHAR(256) NOT NULL"),
        COLOR       (8,     "color",        "VARCHAR(32) NOT NULL"),
        MOTD        (9,     "motd",         "VARCHAR(256) NOT NULL"),
        DESCRIPTION (10,    "description",  "VARCHAR(256) NOT NULL");

        int index;
        String name;
        String signature;

        Table ( int index, String name, String signature ) {
            this.index = index;
            this.name = name;
            this.signature = signature;
        }

        @Override
        public int getIndex() {
            return index;
        }

        @Override
        public String getId() {
            return name;
        }

        @Override
        public String getSignature() {
            return signature;
        }

        @Override
        public String getCreateTableQuery() {
            StringBuilder builder = new StringBuilder();
            builder.append("CREATE TABLE IF NOT EXISTS `towns_Towns` (");
            for ( Table v : values() ) {
                builder.append("`").append(v.getId()).append("` ").append(v.getSignature()).append(",");
            }
            builder.append("PRIMARY KEY (`").append(values()[0].getId()).append("`));");
            return builder.toString();
        }
    }

    public TownManager() {
    }

    @Override
    protected Class<Town> getManagerClass() {
        return Town.class;
    }

    @Override
    protected String getCreateTableQuery() {
        return Table.UUID.getCreateTableQuery();
    }

    @Override
    public String getSelectAllQuery() {
        return "SELECT * FROM `towns_Towns`;";
    }

    @Override
    public String getSelectQuery() {
        return "SELECT * FROM `towns_Towns` WHERE `uuid` = ?;";
    }

    @Override
    public String getInsertQuery() {
        return "REPLACE INTO `towns_Towns` (" +
                "`uuid`," +
                "`status`," +
                "`nation_uuid`," +
                "`flags`," +
                "`maxArea`," +
                "`spawn`," +
                "`name`," +
                "`color`," +
                "`motd`," +
                "`description`" +
                ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
    }

    @Override
    public String getDeleteQuery() {
        return
        "DELETE FROM `towns_Towns` WHERE `uuid` = ?" +
        "DELETE FROM `towns_Plots` WHERE `town_uuid` = ?;" +
        "UPDATE `towns_Residents` SET `townRank` = 0, `town_uuid` = 'NULL' WHERE `town_uuid` = ?;";
    }

    @Override
    public Town load(Map<String, Object> row) {
        String uuid = (String) row.get("uuid");

        PlotFlags flags = Deserialize.plotFlags( (String) row.get("flags"));

        Location spawn = null;
        Optional<Location> loc = Deserialize.location( (String) row.get("spawn"));
        if ( loc.isPresent() ) spawn = loc.get();

        Optional<Nation> n = Optional.empty();
        if ( !row.get("nation_uuid").equals("NULL") ) n = AtherysTowns.getInstance().getNationManager().getByUUID(UUID.fromString((String) row.get("nation_uuid")));

        return Town.fromUUID(UUID.fromString(uuid))
                .name(          (String) row.get("name"))
                .color(                  Deserialize.color((String) row.get("color")) )
                .motd(          (String) row.get("motd"))
                .description(   (String) row.get("description"))
                .maxArea(       (int)    row.get("maxArea"))
                .status(                 TownStatus.fromId( (int) row.get("status")))
                .flags(                  flags)
                .nation(                 n.orElse(null))
                .spawn(                  spawn)
                .build();
    }

    @Override
    public boolean delete(Town obj) {
        return false;
    }

}
