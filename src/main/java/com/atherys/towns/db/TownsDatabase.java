package com.atherys.towns.db;

import com.atherys.towns.AtherysTowns;
import com.atherys.towns.Settings;
import org.spongepowered.api.plugin.Plugin;

import java.io.File;
import java.io.IOException;

public class TownsDatabase {

    public enum Type {
        SQLITE,
        MYSQL;

        protected TownsDatabase create() {
            if ( this == SQLITE ) {
                Plugin annotation = AtherysTowns.getInstance().getClass().getAnnotation(Plugin.class);
                return new TownsDatabase( "config/" + annotation.id(), "towns.db");
            } else {
                return new TownsDatabase( Settings.SQL_HOST, Settings.SQL_PORT, Settings.SQL_DATABASE, Settings.SQL_USER, Settings.SQL_PASSWORD );
            }
        }
    }

    private static final TownsDatabase instance = new TownsDatabase();
    private final String url;

    private TownsDatabase () {
        this.url = Settings.SQL_TYPE.create().url;
    }

    private TownsDatabase (String host, int port, String database, String user, String password) {
        url = "jdbc:mysql://" + host + ":" + port + "/" + database + "?user=" + user + "&password=" + password + "&useSSL=false";
        initDriver("org.mariadb.jdbc.Driver");
    }

    private TownsDatabase (String path, String file) {
        File dbFile = new File(path, file);
        if ( !dbFile.exists() ) {
            try {
                dbFile.getParentFile().mkdirs();
                dbFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        url = "jdbc:sqlite:" + dbFile.getAbsolutePath();
        initDriver("org.sqlite.JDBC");
    }

    String getURL () {
        return url;
    }

    public void saveAll() {
        for ( DatabaseManager mngr : AtherysTowns.getInstance().getDbManagers() ) mngr.saveAll();
    }

    private static void initDriver(final String driver) {
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException | LinkageError e) {
            System.out.println("Database Driver Error: " + e.getMessage());
        }
    }

    public static TownsDatabase getInstance() {
        return instance;
    }
}
