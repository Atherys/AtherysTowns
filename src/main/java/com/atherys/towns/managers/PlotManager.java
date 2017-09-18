package com.atherys.towns.managers;

import com.atherys.towns.AtherysTowns;
import com.atherys.towns.plot.Plot;
import com.atherys.towns.plot.PlotDefinition;
import com.atherys.towns.plot.PlotFlags;
import com.atherys.towns.town.Town;
import com.atherys.towns.utils.DatabaseUtils;
import com.atherys.towns.utils.Deserialize;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public final class PlotManager extends AreaObjectManager<Plot> {

    private static final String CREATE_TABLE_PLOTS =
            "CREATE TABLE IF NOT EXISTS `towns_Plots` (" +
                    "  `uuid` VARCHAR NOT NULL," +
                    "  `town_uuid` VARCHAR NOT NULL," +
                    "  `definition` VARCHAR NOT NULL," +
                    "  `flags` VARCHAR NOT NULL," +
                    "  `name` VARCHAR NOT NULL," +
                    "  PRIMARY KEY (`uuid`)" +
                    ");";

    public enum Table implements DatabaseUtils.AbstractTable<Table> {
        UUID        (1, "uuid",         "VARCHAR(36) NOT NULL"),
        TOWN_UUID   (2, "town_uuid",    "VARCHAR(36)"         ),
        DEFINITION  (3, "definition",   "VARCHAR(256) NOT NULL"),
        FLAGS       (4, "flags",        "VARCHAR(256) NOT NULL"),
        NAME        (5, "name",         "VARCHAR(256) NOT NULL");

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
            builder.append("CREATE TABLE IF NOT EXISTS `towns_Plots` (");
            for ( Table v : values() ) {
                builder.append("`").append(v.getId()).append("` ").append(v.getSignature()).append(",");
            }
            builder.append("PRIMARY KEY (`").append(values()[0].getId()).append("`));");
            return builder.toString();
        }
    }

    public PlotManager() {
    }

    public boolean checkIntersection (PlotDefinition definition ) {
        for ( Plot p : list ) {
            if ( p.getDefinition().intersects(definition) ) return true;
        }
        return false;
    }

    @Override
    protected Class<Plot> getManagerClass() {
        return Plot.class;
    }

    @Override
    protected String getCreateTableQuery() {
        return Table.UUID.getCreateTableQuery();
    }

    @Override
    public String getSelectAllQuery() {
        return "SELECT * FROM `towns_Plots`";
    }

    @Override
    public String getSelectQuery() {
        return "SELECT * FROM `towns_Plots` WHERE `uuid` = ?;";
    }

    @Override
    public String getInsertQuery() { return "REPLACE INTO `towns_Plots` (`uuid`,`town_uuid`,`definition`,`flags`,`name`) VALUES ( ?, ?, ?, ?, ? );"; }

    @Override
    public String getDeleteQuery() {
        return "DELETE FROM `towns_Plots` WHERE `uuid` = ?";
    }

    @Override
    public Plot load(Map<String, Object> row) {
        String town_uuid = (String) row.get("town_uuid");
        if (Objects.equals(town_uuid, "NULL")) {
            AtherysTowns.getInstance().getLogger().error("Plot town " + town_uuid + " @ row " + row.toString() + " is invalid. Will skip.");
            return null;
        }

        Optional<Town> t = AtherysTowns.getInstance().getTownManager().getByUUID(UUID.fromString(town_uuid));

        if ( !t.isPresent() ) {
            AtherysTowns.getInstance().getLogger().error("Plot town " + town_uuid + " @ row " + row.toString() + " does not exist. Will skip.");
            return null;
        }

        Optional<PlotDefinition> define = Deserialize.definition((String) row.get("definition"));
        if ( !define.isPresent() ) {
            AtherysTowns.getInstance().getLogger().error("Plot definition " + row.get("definition") + " @ row " + row.toString() + " failed to deserialize. Will skip.");
            return null;
        }

        PlotFlags flags = Deserialize.plotFlags((String) row.get("flags"));

        return Plot.fromUUID(UUID.fromString((String) row.get("uuid")))
                .town(t.get())
                .name((String) row.get("name"))
                .definition(define.get())
                .flags(flags)
                .build();
    }

    @Override
    public boolean delete(Plot obj) {
        return false;
    }
}
