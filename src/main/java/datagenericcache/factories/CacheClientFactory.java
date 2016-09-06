package datagenericcache.factories;

import datagenericcache.providers.CacheProvider;
import datagenericcache.providers.LocalMemoryProvider;

import java.util.List;

public class CacheClientFactory {
    private final String ACTIVE_CACHE_PROVIDER = "CacheClientFactory.ActiveCacheProvider";

    private CacheProvider localMemoryProvider;

    public CacheClientFactory() {
        localMemoryProvider = new LocalMemoryProvider();
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

        providers
                .forEach(provider ->
                {
                    try {
                        CacheProvider cacheProvider = (CacheProvider)provider.newInstance();

                        if (isWorking(cacheProvider)) {
                            localMemoryProvider.add(ACTIVE_CACHE_PROVIDER, cacheProvider, null);
                        }
                    } catch (InstantiationException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                });

        if (availableCacheProvider == null) {
            availableCacheProvider = localMemoryProvider;
        }

        return availableCacheProvider;
    }

    private CacheProvider GetActiveCacheProvider() {
        CacheProvider activeProvider = null;

        if (localMemoryProvider.exists(ACTIVE_CACHE_PROVIDER)) {
            activeProvider = localMemoryProvider.retrieve(ACTIVE_CACHE_PROVIDER);
        }

        return activeProvider;
    }

    private boolean isWorking(CacheProvider cacheProvider) {
        final String value = "isWorking";

        String key = java.util.UUID.randomUUID().toString();
        boolean isWorking = false;

        try {
            cacheProvider.add(key, value, null);
            isWorking = value == cacheProvider.retrieve(key);
            cacheProvider.remove(key);
        }
        catch (Exception exception) {
            isWorking = false;
        }

        return isWorking;
    }
}
