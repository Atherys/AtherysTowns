package com.atherys.towns.db;

import com.atherys.towns.AtherysTowns;
import com.atherys.towns.base.TownsObject;
import com.atherys.towns.utils.DatabaseUtils;
import com.atherys.towns.utils.Deserialize;
import com.atherys.towns.utils.Serialize;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

// SQL is bad. Let's do MongoDB instead.
public abstract class DatabaseManager<T extends TownsObject> {

    private static Connection connection = null;

    private static Connection getConnection () throws SQLException {
        if ( connection == null || connection.isClosed() ) connection = DriverManager.getConnection( AtherysTowns.getInstance().getDatabase().getURL() );
        return connection;
    }

    private static void closeConnection() {
        DatabaseUtils.close(connection);
    }

    public void setup() {
        PreparedStatement statement = null;
        try {
            statement = getConnection().prepareStatement(  getCreateTableQuery() );
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtils.close(statement);
            closeConnection();
        }
        loadAll();
    }

    private static final String CREATE_TABLE_WILDERNESS =
            "CREATE TABLE IF NOT EXISTS `towns_Wilderness` (" +
                    "  `location` VARCHAR(256) NOT NULL," +
                    "  `snapshot` VARCHAR(512) NOT NULL," +
                    "  `timestamp` BIGINT NOT NULL," +
                    "  PRIMARY KEY (`location`)" +
                    ");";

    public static void createWildernessRegenTable() {
        PreparedStatement statement = null;
        try {
            statement = getConnection().prepareStatement(  CREATE_TABLE_WILDERNESS );
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtils.close(statement);
            closeConnection();
        }
    }

    protected abstract Class<T> getManagerClass();

    protected abstract String getCreateTableQuery();

    protected abstract String getSelectAllQuery();

    protected abstract String getSelectQuery();

    protected abstract String getInsertQuery();

    protected abstract String getDeleteQuery();

    protected abstract List<T> getAll();

    protected abstract T load ( Map<String,Object> row );

    public void save(T obj) {
        PreparedStatement statement = null;
        try {
            statement = DatabaseUtils.populate(getConnection().prepareStatement(getInsertQuery()), obj.toDatabaseStorable());
            statement.execute();
        } catch ( SQLException e ) {
            AtherysTowns.getInstance().getLogger().error("Database Error while saving " + obj.getClass().getSimpleName() + ": " + e.getMessage());
        } finally {
            DatabaseUtils.close(statement);
            closeConnection();
        }
    }

    protected abstract boolean delete ( T obj );

    private void loadAll() {
        PreparedStatement statement = null;
        int i = 0;

        try {
            String query = this.getSelectAllQuery();
            statement = getConnection().prepareStatement(query);
            if ( statement.execute() ) {
                for ( Map<String,Object> row : DatabaseUtils.map ( statement.getResultSet() ) ) {
                    this.load(row);
                    i++;
                }
            }
        } catch ( SQLException e ) {
            AtherysTowns.getInstance().getLogger().error("Database Error while loading " + getManagerClass().getSimpleName() + ": " + e.getMessage() );
        } finally {
            DatabaseUtils.close(statement);
            closeConnection();
        }

        AtherysTowns.getInstance().getLogger().info( "Successfully loaded " + i + " " + getManagerClass().getSimpleName() + "(s)" );
    }

    public void saveAll ( ) {
        int i = 0;

        for ( T obj : this.getAll() ) {
            this.save( obj );
        }

        AtherysTowns.getInstance().getLogger().info( "Successfully saved " + i + " " + getManagerClass().getSimpleName() + "(s)" );
    }

    /* WILDERNESS REGEN */

    public static void saveSnapshot (Location<World> loc, BlockSnapshot snapshot, long timestamp) {
        PreparedStatement statement = null;
        try {
            statement = getConnection().prepareStatement("INSERT IGNORE INTO `towns_Wilderness` (`location`,`snapshot`,`timestamp`) VALUES (?, ?, ?);");
            String snap = Serialize.blockSnapshot(snapshot);
            statement.setString(1, Serialize.location(loc).toString());
            statement.setString(2, snap);
            statement.setLong(3, timestamp);
            statement.execute();
        } catch ( SQLException e ) {
            e.printStackTrace();
        } finally {
            DatabaseUtils.close(statement);
            closeConnection();
        }
    }

    public static List<BlockSnapshot> loadSnapshotsBeforeTimestamp ( long timestamp ) {

        PreparedStatement statement = null;
        int i = 0;
        List<BlockSnapshot> snaps = new LinkedList<>();

        try {
            statement = getConnection().prepareStatement("SELECT * FROM `towns_Wilderness` WHERE `timestamp` <= ?;");
            statement.setLong(1, timestamp);
            if ( statement.execute() ) {
                List<Map<String,Object>> result = DatabaseUtils.map(statement.getResultSet());
                for ( Map<String,Object> row : result ) {
                    snaps.add(Deserialize.blockSnapshot( (String) row.get("snapshot") ) );
                    i++;
                }
            }
        } catch ( SQLException e ) {
            e.printStackTrace();
        } finally {
            DatabaseUtils.close(statement);
            closeConnection();
        }

        deleteSnapshotsBeforeTimestamp(timestamp);
        AtherysTowns.getInstance().getLogger().info("[WILDERNESS] Loaded " + i + " ( " + snaps.size() + " ) snapshots for before " + timestamp);
        return snaps;
    }

    private static void deleteSnapshotsBeforeTimestamp ( long timestamp ) {
        PreparedStatement statement = null;
        try {
            statement = getConnection().prepareStatement("DELETE FROM `towns_Wilderness` WHERE `timestamp` <= ?;");
            statement.setLong(1, timestamp);
            statement.execute();
        } catch ( SQLException e ) {
            e.printStackTrace();
        } finally {
            DatabaseUtils.close(statement);
            closeConnection();
        }
    }

}
