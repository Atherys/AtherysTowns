package com.atherys.towns.persistence.cache;

import com.atherys.core.db.cache.Cache;
import com.atherys.core.db.cache.SimpleCache;
import com.atherys.towns.model.entity.Poll;
import com.atherys.towns.persistence.PollRepository;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class PollCache extends SimpleCache<Poll, Long> {

    @Inject
    private PollRepository pollRepository;

    private Cache<Poll, Long> pollCache = new SimpleCache<>();

    public Cache<Poll, Long> getPollCache() { return this.pollCache; }

    public PollCache() {
    }

    public void initCache() {
        pollRepository.initCache();
    }

    public void flushCache() {
        pollRepository.flushCache();
    }

}
