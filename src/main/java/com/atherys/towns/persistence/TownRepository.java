package com.atherys.towns.persistence;

import com.atherys.core.db.AtherysRepository;
import com.atherys.towns.entity.Town;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.spongepowered.api.text.Text;

import java.util.Optional;
import java.util.UUID;

@Singleton
public class TownRepository extends AtherysRepository<Town, UUID> {
    @Inject
    protected TownRepository(Logger logger) {
        super(Town.class, logger);
    }

    public Optional<Town> findByName(String townName) {
        Text textName = Text.of(townName);
        return cacheParallelStream().filter(town -> town.getName().equals(textName)).findFirst();
    }
}
