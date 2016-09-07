package org.datagenericcache.factories;

import org.datagenericcache.providers.CacheProvider;
import org.datagenericcache.providers.LocalMemoryProvider;

import java.util.Iterator;
import java.util.List;

public class CacheClientFactory {
    private final String ACTIVE_CACHE_PROVIDER = "CacheClientFactory.ActiveCacheProvider";

    private CacheProvider localMemoryProvider;
    private InstanceFactory instanceFactory;

    public CacheClientFactory() {
        this(new LocalMemoryProvider(), new InstanceFactory());
    }

    public CacheClientFactory(CacheProvider cacheProvider, InstanceFactory instanceFactory) {
        localMemoryProvider = cacheProvider;
        this.instanceFactory = instanceFactory;
    }

    public CacheProvider create(List<Class> providers) {
        CacheProvider activeCacheProvider = GetActiveCacheProvider();

        if (activeCacheProvider == null) {
            activeCacheProvider = GetAvailableCacheProvider(providers);
        }

        return activeCacheProvider;
    }

    private CacheProvider GetAvailableCacheProvider(List<Class> providers) {
        CacheProvider availableCacheProvider = null;
        Iterator<Class> iterator = providers.iterator();

        while (availableCacheProvider == null && iterator.hasNext()) {
            Class provider = iterator.next();
            CacheProvider cacheProvider = instanceFactory.create(provider);

            if (cacheProvider != null && isWorking(cacheProvider)) {
                localMemoryProvider.add(ACTIVE_CACHE_PROVIDER, cacheProvider, null);
                availableCacheProvider = cacheProvider;
            }
        }

        if (availableCacheProvider == null) {
            availableCacheProvider = localMemoryProvider;
        }

        return availableCacheProvider;
    }

    private CacheProvider GetActiveCacheProvider() {
        CacheProvider activeProvider = null;

        if (localMemoryProvider.exists(ACTIVE_CACHE_PROVIDER)) {
            activeProvider = localMemoryProvider.retrieve(ACTIVE_CACHE_PROVIDER);

            if (!(isWorking(activeProvider))) {
                localMemoryProvider.remove(ACTIVE_CACHE_PROVIDER);
                return GetActiveCacheProvider();
            }
        }

        return activeProvider;
    }

    private boolean isWorking(CacheProvider cacheProvider) {
        final String value = "isWorking";

        String key = java.util.UUID.randomUUID().toString();
        boolean isWorking;

        try {
            cacheProvider.add(key, value, null);
            String retrieved = cacheProvider.retrieve(key);
            isWorking = retrieved.equals(value);
            cacheProvider.remove(key);
        }
        catch (Exception exception) {
            isWorking = false;
        }

        return isWorking;
    }
}
