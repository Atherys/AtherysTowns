package com.atherys.towns.managers;

import com.atherys.towns.AtherysTowns;
import com.atherys.towns.db.DatabaseManager;
import com.atherys.towns.nation.Nation;
import com.atherys.towns.resident.Resident;
import com.atherys.towns.resident.ranks.NationRank;
import com.atherys.towns.resident.ranks.TownRank;
import com.atherys.towns.town.Town;
import com.atherys.towns.utils.DatabaseUtils;
import com.atherys.towns.utils.UserUtils;
import org.spongepowered.api.entity.living.player.User;

import java.util.*;

public final class ResidentManager extends DatabaseManager<Resident> {

    private static final String CREATE_TABLE_RESIDENTS =
            "CREATE TABLE IF NOT EXISTS `towns_Residents` (" +
                    "  `uuid` VARCHAR NOT NULL," +
                    "  `town_uuid` VARCHAR," +
                    "  `townRank` INTEGER NOT NULL," +
                    "  `nationRank` INTEGER NOT NULL," +
                    "  `registerTimestamp` INTEGER NOT NULL," +
                    "  `lastOnlineTimestamp` INTEGER NOT NULL," +
                    "  PRIMARY KEY (`uuid`)" +
                    ");";

    public List<Resident> getByTown(Town town) {
        List<Resident> residents = new ArrayList<>();
        for ( Resident res : playerResidentMap.values() ) {
            if ( res.town().isPresent() && res.town().get().equals(town) ) {
                residents.add(res);
            }
        }
        return residents;
    }

    public List<Resident> getByNation(Nation nation) {
        List<Resident> residents = new ArrayList<>();
        for ( Resident res : playerResidentMap.values() ) {
            Optional<Town> town = res.town();
            if ( town.isPresent() ) {
                if ( town.get().getParent().isPresent() && town.get().getParent().get().equals(nation) ) residents.add(res);
            }
        }
        return residents;
    }

    public enum Table implements DatabaseUtils.AbstractTable<Table> {
        UUID        (1, "uuid",                 "VARCHAR(36) NOT NULL"),
        TOWN_UUID   (2, "town_uuid",            "VARCHAR(36)"         ),
        TOWN_RANK   (3, "townRank",             "INTEGER NOT NULL"),
        NATION_RANK (4, "nationRank",           "INTEGER NOT NULL"),
        REGISTER    (5, "registerTimestamp",    "BIGINT NOT NULL"),
        LAST_ONLINE (6, "lastOnlineTimestamp",  "BIGINT NOT NULL");

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
            builder.append("CREATE TABLE IF NOT EXISTS `towns_Residents` (");
            for ( Table v : values() ) {
                builder.append("`").append(v.getId()).append("` ").append(v.getSignature()).append(",");
            }
            builder.append("PRIMARY KEY (`").append(values()[0].getId()).append("`) );");
            return builder.toString();
        }


    }

    private Map<UUID,Resident> playerResidentMap = new HashMap<>();;

    public ResidentManager () {
    }

    public Optional<Resident> get ( UUID uuid ) {
        Resident res = playerResidentMap.get(uuid);
        if ( res == null ) return Optional.empty();
        return Optional.of(playerResidentMap.get(uuid));
    }

    public void add ( UUID uuid, Resident resident ) { playerResidentMap.put(uuid, resident); }

    public void remove ( UUID uuid ) { playerResidentMap.remove(uuid); }

    public boolean has ( UUID uuid ) { return playerResidentMap.containsKey(uuid); }

    @Override
    protected Class<Resident> getManagerClass() {
        return Resident.class;
    }

    @Override
    protected String getCreateTableQuery() {
        return Table.UUID.getCreateTableQuery();
    }

    @Override
    public String getSelectAllQuery() {
        return "SELECT * FROM `towns_Residents`;";
    }

    @Override
    public String getSelectQuery() {
        return "SELECT * FROM `towns_Residents` WHERE `uuid` = ?;";
    }

    @Override
    public String getInsertQuery() { return "REPLACE INTO `towns_Residents` (`uuid`,`town_uuid`,`townRank`,`nationRank`,`registerTimestamp`,`lastOnlineTimestamp`) VALUES ( ?, ?, ?, ?, ?, ? );"; }

    @Override
    public String getDeleteQuery() {
        return "DELETE FROM `towns_Residents` WHERE `uuid` = ?;";
    }

    @Override
    public List<Resident> getAll() {
        return new LinkedList<>(playerResidentMap.values());
    }

    @Override
    public Resident load( Map<String, Object> row ) {

        Town t;
        TownRank tRank = TownRank.fromId((int) row.get("townRank"));
        NationRank nRank = NationRank.fromId((int) row.get("nationRank"));
        String town_uuid = (String) row.get("town_uuid");

        if (Objects.equals(town_uuid, "NULL")) {
            t = null;
        } else {
            Optional<Town> tOpt = AtherysTowns.getInstance().getTownManager().getByUUID(UUID.fromString(town_uuid));
            if ( tOpt.isPresent() ) {
                t = tOpt.get();
            } else {
                t = null;
                Optional<? extends User> u = UserUtils.getUser(UUID.fromString((String) row.get("uuid")));
                if ( u.isPresent() ) {
                    AtherysTowns.getInstance().getLogger().error("Resident " + u.get().getName() + " ( " + row.get("uuid") + " ) belongs to town with UUID " + town_uuid + " which does not exist. Resident will not be added to town and town and nation ranks will be set to none" );
                    tRank = TownRank.NONE;
                    nRank = NationRank.NONE;
                }
            }
        }

        return Resident.fromUUID(UUID.fromString((String) row.get("uuid")))
                .town(
                        t,
                        tRank
                )
                .townRank(tRank)
                .nationRank(nRank)
                .registerTimestamp((int) row.get("registerTimestamp"))
                .updateLastOnline()
                .build();
    }

    @Override
    public boolean delete(Resident obj) {
        return false;
    }

    public Map<UUID, Resident> getMapAll() {
        return playerResidentMap;
    }
}
