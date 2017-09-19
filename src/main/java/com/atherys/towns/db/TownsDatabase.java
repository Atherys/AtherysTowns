package com.atherys.towns.db;

import com.atherys.towns.Settings;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;

import java.util.Arrays;

public class TownsDatabase {

    private static TownsDatabase instance = new TownsDatabase();

    MongoClient client;
    MongoDatabase db;

    private TownsDatabase() {
        MongoCredential credential = MongoCredential.createCredential( Settings.DB_USER, Settings.DB_USER_DB, Settings.DB_PASSWORD.toCharArray() );
        client = new MongoClient( new ServerAddress(Settings.DB_HOST, Settings.DB_PORT), Arrays.asList(credential) );
        db = client.getDatabase(Settings.DB_DATABASE);
    }

    public MongoDatabase getDatabase() {
        return db;
    }

    public static TownsDatabase getInstance() {
        return instance;
    }

}
