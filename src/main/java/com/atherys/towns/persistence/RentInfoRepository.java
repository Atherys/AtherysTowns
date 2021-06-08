package com.atherys.towns.persistence;

import com.atherys.core.db.CachedHibernateRepository;
import com.atherys.towns.model.entity.RentInfo;
import com.atherys.towns.persistence.cache.TownsCache;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class RentInfoRepository extends CachedHibernateRepository<RentInfo, Long> {
    private TownsCache townsCache;

    @Inject
    public RentInfoRepository(TownsCache townsCache) {
        super(RentInfo.class);
        super.cache = townsCache.getRentInfoCache();
        this.townsCache = townsCache;
    }
}
