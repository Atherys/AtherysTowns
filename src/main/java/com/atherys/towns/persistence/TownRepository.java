package com.atherys.towns.persistence;

import com.atherys.core.db.HibernateRepository;
import com.atherys.towns.entity.Plot;
import com.atherys.towns.entity.Resident;
import com.atherys.towns.entity.Town;
import com.google.inject.Singleton;
import org.spongepowered.api.text.Text;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

@Singleton
public class TownRepository extends HibernateRepository<Town, Long> {
    protected TownRepository() {
        super(Town.class);
    }

    public Optional<Town> findByName(String townName) {
        Text textName = Text.of(townName);
        return getCache().values().parallelStream().filter(town -> town.getName().equals(textName)).findFirst();
    }

    private <R> R queryEntityManager(Function<EntityManager, R> consumer) {
        EntityManager em = sessionFactory.getCurrentSession();
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        R result = consumer.apply(em);
        transaction.commit();
        em.close();
        return result;
    }

    public CompletableFuture<List<Plot>> getTownPlotsAsync(Town town) {
        return CompletableFuture.supplyAsync(() -> queryEntityManager((em) -> {
            TypedQuery<Plot> query = em.createQuery("SELECT p FROM Plot p WHERE p.town = :town", Plot.class);
            query.setParameter("town", town);
            return query.getResultList();
        }));
    }

    public CompletableFuture<List<Resident>> getTownResidentsAsync(Town town) {
        return CompletableFuture.supplyAsync(() -> queryEntityManager((em) -> {
            TypedQuery<Resident> query = em.createQuery("SELECT r FROM Resident r WHERE r.town = :town", Resident.class);
            query.setParameter("town", town);
            return query.getResultList();
        }));
    }
}
