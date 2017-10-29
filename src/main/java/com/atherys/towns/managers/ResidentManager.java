package com.atherys.towns.managers;

import com.atherys.towns.AtherysTowns;
import com.atherys.towns.nation.Nation;
import com.atherys.towns.resident.Resident;
import com.atherys.towns.permissions.ranks.NationRank;
import com.atherys.towns.permissions.ranks.TownRank;
import com.atherys.towns.town.Town;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

import java.util.*;

public final class ResidentManager extends DatabaseManager<Resident> {

    private static ResidentManager instance = new ResidentManager();

    public List<Resident> getByTown(Town town) {
        List<Resident> residents = new ArrayList<>();
        for ( Resident res : playerResidentMap.values() ) {
            if ( res.getTown().isPresent() && res.getTown().get().equals(town) ) {
                residents.add(res);
            }
        }
        return residents;
    }

    public List<Resident> getByNation(Nation nation) {
        List<Resident> residents = new ArrayList<>();
        for ( Resident res : playerResidentMap.values() ) {
            Optional<Town> town = res.getTown();
            if ( town.isPresent() ) {
                if ( town.get().getParent().isPresent() && town.get().getParent().get().equals(nation) ) residents.add(res);
            }
        }
        return residents;
    }

    private Map<UUID,Resident> playerResidentMap = new HashMap<>();

    private ResidentManager () {
    }

    public Optional<Resident> get ( UUID uuid ) {
        Resident res = playerResidentMap.get(uuid);
        if ( res == null ) return Optional.empty();
        return Optional.of(playerResidentMap.get(uuid));
    }

    public void add ( UUID uuid, Resident resident ) { playerResidentMap.put(uuid, resident); }

    public boolean has ( UUID uuid ) { return playerResidentMap.containsKey(uuid); }

    @Override
    public MongoCollection<Document> collection() {
        return AtherysTowns.getDb().getCollection("residents");
    }

    @Override
    public Document toDocument(Resident object) {
        Document doc = new Document( "uuid", object.getUUID() );

        if ( object.getTown().isPresent() ) {
            doc.append( "town", object.getTown().get().getUUID() );
        } else {
            doc.append( "town", null );
        }

        doc.append("registered", object.getRegisteredSeconds());
        doc.append("last_online", object.getLastOnlineSeconds());
        doc.append("town_rank", object.getTownRank().id() );
        doc.append("nation_rank", object.getNationRank().id() );

        return doc;
    }

    @Override
    public boolean fromDocument ( Document doc ) {
        UUID uuid =  doc.get("uuid", UUID.class);//UUID.fromString(doc.getString("uuid"));

        Resident.Builder builder = Resident.fromUUID(uuid);

        UUID town_uuid = doc.get("town", UUID.class);
        if ( town_uuid != null ) {
            Optional<Town> t = TownManager.getInstance().getByUUID(town_uuid);
            TownRank rank = TownRank.fromId(doc.getInteger("town_rank"));
            t.ifPresent(town -> {
                builder.town(town, rank);
                builder.nationRank(NationRank.fromId(doc.getInteger("nation_rank")));
            });
        }
        builder.registerTimestamp( doc.getLong("registered") );
        builder.updateLastOnline();
        builder.build();

        return true;
    }

    @Override
    public void saveAll() {
        this.saveAll(playerResidentMap.values());
    }

    public static ResidentManager getInstance() {
        return instance;
    }
}
