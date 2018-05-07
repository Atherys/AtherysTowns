package com.atherys.towns.managers;

import com.atherys.core.database.mongo.AbstractMongoDatabaseManager;
import com.atherys.towns.AtherysTowns;
import com.atherys.towns.db.TownsDatabase;
import com.atherys.towns.nation.Nation;
import com.atherys.towns.permissions.ranks.*;
import com.atherys.towns.resident.Resident;
import com.atherys.towns.resident.ResidentBuilder;
import com.atherys.towns.town.Town;
import org.bson.Document;

import java.util.*;

public final class ResidentManager extends AbstractMongoDatabaseManager<Resident> {

    private static ResidentManager instance = new ResidentManager();

    private ResidentManager() {
        super(AtherysTowns.getInstance().getLogger(), TownsDatabase.getInstance(), "residents");
    }

    public static ResidentManager getInstance() {
        return instance;
    }

    public List<Resident> getByTown(Town town) {
        List<Resident> residents = new ArrayList<>();
        for (Resident res : super.getCache().values()) {
            if (res.getTown().isPresent() && res.getTown().get().equals(town)) {
                residents.add(res);
            }
        }
        return residents;
    }

    public List<Resident> getByNation(Nation nation) {
        List<Resident> residents = new ArrayList<>();
        for (Resident res : super.getCache().values()) {
            Optional<Town> town = res.getTown();
            if (town.isPresent()) {
                if (town.get().getParent().isPresent() && town.get().getParent().get()
                        .equals(nation)) {
                    residents.add(res);
                }
            }
        }
        return residents;
    }

    public boolean has(UUID uuid) {
        return super.getCache().containsKey(uuid);
    }

    public Collection<Resident> getAll() {
        return super.getCache().values();
    }

    @Override
    public Optional<Document> toDocument(Resident object) {
        Document doc = new Document("uuid", object.getUUID());

        if (object.getTown().isPresent()) {
            doc.append("town", object.getTown().get().getUUID());
        } else {
            doc.append("town", null);
        }

        doc.append("registered", object.getRegisteredDate());
        doc.append("last_online", object.getLastOnlineDate());
        doc.append("town_rank", object.getTownRank().getId());
        doc.append("nation_rank", object.getNationRank().getId());

        return Optional.of(doc);
    }

    @Override
    public Optional<Resident> fromDocument(Document doc) {
        UUID uuid = doc.get("uuid", UUID.class);//UUID.fromString(doc.getString("uuid"));

        ResidentBuilder builder = Resident.fromUUID(uuid);

        UUID town_uuid = doc.get("town", UUID.class);
        if (town_uuid != null) {
            Optional<Town> t = TownManager.getInstance().getByUUID(town_uuid);
            Optional<TownRank> rank = TownRankRegistry.getInstance()
                    .getById(doc.getString("town_rank"));
            t.ifPresent(town -> {
                builder.town(town, rank.orElse(TownRanks.RESIDENT));
                builder.nationRank(
                        NationRankRegistry.getInstance().getById(doc.getString("nation_rank"))
                                .orElse(NationRanks.RESIDENT));
            });
        }
        builder.registerTimestamp(doc.getDate("registered"));
        builder.updateLastOnline();
        return Optional.of(builder.build());
    }
}
