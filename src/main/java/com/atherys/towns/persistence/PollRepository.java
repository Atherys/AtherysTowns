package com.atherys.towns.persistence;

import com.atherys.core.db.CachedHibernateRepository;
import com.atherys.towns.model.entity.Poll;
import com.atherys.towns.persistence.cache.PollCache;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.Collection;
import java.util.Optional;

@Singleton
public class PollRepository extends CachedHibernateRepository<Poll, Long> {

    @Inject
    private PollCache pollCache;

    @Inject
    protected PollRepository(PollCache pollCache) {
        super(Poll.class);
        super.cache = pollCache.getPollCache();
        this.pollCache = pollCache;
    }

    @Override
    public void initCache() {
        pollCache.getAll().forEach(poll -> cache.add(poll));
    }

    public Optional<Poll> findByName(String pollName) {
        return cache.findOne(t -> t.getPollName().equals(pollName));
    }

    public Collection<Poll> getAll() {
        return pollCache.getPollCache().getAll();
    }

}
