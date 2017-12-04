package com.junliang.spring.config;

import org.springframework.boot.autoconfigure.cache.JCacheManagerCustomizer;
import org.springframework.stereotype.Component;

import javax.cache.CacheManager;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.expiry.Duration;
import javax.cache.expiry.TouchedExpiryPolicy;
import java.util.concurrent.TimeUnit;

@Component
public class Ehcache3Config implements JCacheManagerCustomizer {
    private static final String NAME_CACHE = "people";

    @Override
    public void customize(CacheManager cacheManager) {
        cacheManager.createCache(NAME_CACHE,
                new MutableConfiguration<>()
                        .setExpiryPolicyFactory(TouchedExpiryPolicy.factoryOf(new Duration(TimeUnit.SECONDS, 10)))
                        .setStoreByValue(true).setStatisticsEnabled(true));
    }
}
