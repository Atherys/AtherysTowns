package com.atherys.towns.persistence;

import com.atherys.core.db.HibernateRepository;
import com.atherys.towns.entity.Town;
import com.google.inject.Singleton;
import org.spongepowered.api.text.Text;

import java.util.Optional;
import java.util.UUID;

@Singleton
public class TownRepository extends HibernateRepository<Town, UUID> {
    protected TownRepository() {
        super(Town.class);
    }

    public Optional<Town> findByName(String townName) {
        Text textName = Text.of(townName);
        return getCache().values().parallelStream().filter(town -> town.getName().equals(textName)).findFirst();
    }
}
