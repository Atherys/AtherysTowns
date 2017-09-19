package com.atherys.towns.managers;

import com.atherys.towns.db.TownsDatabase;
import com.atherys.towns.nation.Nation;
import com.atherys.towns.plot.Plot;
import com.atherys.towns.resident.Resident;
import com.atherys.towns.town.Town;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

import java.util.Optional;

public final class NationManager extends AreaObjectManager<Nation> {

    private static NationManager instance = new NationManager();

    private NationManager() {
    }

    public Optional<Nation> getByPlot ( Plot plot ) {
        return getByTown(plot.getTown());
    }

    public Optional<Nation> getByResident ( Resident res ) {
        if ( res.getTown().isPresent() ) {
            return res.getTown().get().getParent();
        }
        return Optional.empty();
    }

    public Optional<Nation> getByTown ( Town town ) {
        return town.getParent();
    }

    @Override
    public MongoCollection<Document> collection() {
        return TownsDatabase.getInstance().getDatabase().getCollection("nations");
    }

    @Override
    public Document toDocument(Nation object) {
        // TODO: Convert Nation to BSON
        return null;
    }

    @Override
    public void saveAll() {
        super.saveAll(list);
    }

    @Override
    public boolean fromDocument(Document doc) {
        // TODO: Convert BSON to Nation
        return false;
    }

    public static NationManager getInstance() {
        return instance;
    }
}