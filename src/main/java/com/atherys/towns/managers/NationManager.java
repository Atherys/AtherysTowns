package com.atherys.towns.managers;

import com.atherys.towns.nation.Nation;
import com.atherys.towns.plot.Plot;
import com.atherys.towns.resident.Resident;
import com.atherys.towns.town.Town;
import com.atherys.towns.utils.DatabaseUtils;
import com.atherys.towns.utils.Deserialize;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public final class NationManager extends AreaObjectManager<Nation> {

    private static final String CREATE_TABLE_NATIONS =
            "CREATE TABLE IF NOT EXISTS `towns_Nations` (" +
                    "  `uuid` VARCHAR NOT NULL," +
                    "  `name` VARCHAR NOT NULL," +
                    "  `leaderTitle` VARCHAR NOT NULL," +
                    "  `color` VARCHAR NOT NULL," +
                    "  `description` VARCHAR NOT NULL," +
                    "  `tax` FLOAT NOT NULL," +
                    "  `allies_uuids` VARCHAR NOT NULL," +
                    "  `enemies_uuids` VARCHAR NOT NULL," +
                    "  PRIMARY KEY (`uuid`)" +
                    ");";

    public enum Table implements DatabaseUtils.AbstractTable<Table> {
        UUID            (1, "uuid",             "VARCHAR(36) NOT NULL"),
        NAME            (2, "name",             "VARCHAR(256) NOT NULL"),
        LEADER_TITLE    (3, "leaderTitle",      "VARCHAR(256) NOT NULL"),
        COLOR           (4, "color",            "VARCHAR(32) NOT NULL"),
        DESCRIPTION     (5, "description",      "VARCHAR(256) NOT NULL"),
        TAX             (6, "tax",              "FLOAT NOT NULL"  ),
        ALLIES_UUIDS    (7, "allies_uuids",     "VARCHAR(2048) NOT NULL"),
        ENEMIES_UUIDS   (8, "enemies_uuids",    "VARCHAR(2048) NOT NULL");

        int index;
        String id;
        String sig;

        Table ( int index, String id, String signature ) {
            this.index = index;
            this.id = id;
            this.sig = signature;
        }

        @Override
        public int getIndex() {
            return index;
        }

        @Override
        public String getId() {
            return id;
        }

        @Override
        public String getSignature() {
            return sig;
        }

        @Override
        public String getCreateTableQuery() {
            StringBuilder builder = new StringBuilder();
            builder.append("CREATE TABLE IF NOT EXISTS `towns_Nations` (");
            for ( Table v : values() ) {
                builder.append("`").append(v.getId()).append("` ").append(v.getSignature()).append(",");
            }
            builder.append("PRIMARY KEY (`").append(values()[0].getId()).append("`));");
            return builder.toString();
        }
    }

    public NationManager() {
    }

    public Optional<Nation> getByPlot ( Plot plot ) {
        return getByTown(plot.getParent().get());
    }

    public Optional<Nation> getByResident ( Resident res ) {
        Optional<Town> town = res.town();
        return town.flatMap(this::getByTown);
    }

    public Optional<Nation> getByTown ( Town town ) {
        for ( Nation n : list ) {
            if ( n.hasTown(town) ) return Optional.of(n);
        }
        return Optional.empty();
    }

    @Override
    protected Class<Nation> getManagerClass() {
        return Nation.class;
    }

    @Override
    protected String getCreateTableQuery() {
        return Table.UUID.getCreateTableQuery();
    }

    @Override
    public String getSelectAllQuery() {
        return "SELECT * FROM `towns_Nations`;";
    }

    @Override
    public String getSelectQuery() {
        return "SELECT * FROM `towns_Nations` WHERE `uuid` = ?";
    }

    @Override
    public String getInsertQuery() {
        return "REPLACE INTO `towns_Nations` (`uuid`,`name`,`leaderTitle`,`color`,`description`,`tax`,`allies_uuids`,`enemies_uuids`) VALUES ( ?, ?, ?, ?, ?, ?, ?, ? );";
    }

    @Override
    public String getDeleteQuery() {
        return "DELETE FROM `towns_Nations` WHERE `uuid` = ?;";
    }

    @Override
    public Nation load(Map<String, Object> row) {

        Nation n = Nation.fromUUID(UUID.fromString( (String) row.get("uuid")))
                .name( (String) row.get("name"))
                .leaderTitle( (String) row.get("leaderTitle"))
                .color( Deserialize.color( (String) row.get("color") ) )
                .description( (String) row.get("description"))
                .build();


        return n;
    }

    @Override
    public boolean delete(Nation obj) {
        return false;
    }
}