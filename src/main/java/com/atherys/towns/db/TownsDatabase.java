package com.atherys.towns.db;

import com.atherys.towns.TownsConfig;
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
        MongoCredential credential = MongoCredential.createCredential( TownsConfig.DB_USER, TownsConfig.DB_USER_DB, TownsConfig.DB_PASSWORD.toCharArray() );
        client = new MongoClient( new ServerAddress(TownsConfig.DB_HOST, TownsConfig.DB_PORT), Arrays.asList(credential) );
        db = client.getDatabase(TownsConfig.DB_DATABASE);
    }

    public MongoDatabase getDatabase() {
        return db;
    }

    public static TownsDatabase getInstance() {
        return instance;
    }

}
