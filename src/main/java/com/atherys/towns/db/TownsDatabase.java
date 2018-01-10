package com.atherys.towns.db;

import com.atherys.towns.AtherysTowns;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;

import java.util.Arrays;

public class TownsDatabase {

    private static TownsDatabase instance = new TownsDatabase();

    private MongoClient client;
    private MongoDatabase db;

    private TownsDatabase() { }

    public void init() {
        MongoCredential credential = MongoCredential.createCredential( AtherysTowns.getConfig().DATABASE.USERNAME, AtherysTowns.getConfig().DATABASE.USER_DB, AtherysTowns.getConfig().DATABASE.PASSWORD.toCharArray() );
        client = new MongoClient( new ServerAddress( AtherysTowns.getConfig().DATABASE.HOST, AtherysTowns.getConfig().DATABASE.PORT ), Arrays.asList(credential) );
        db = client.getDatabase( AtherysTowns.getConfig().DATABASE.NAME );
    }

    public MongoDatabase getDatabase() {
        return db;
    }

    public static TownsDatabase getInstance() {
        return instance;
    }

}
