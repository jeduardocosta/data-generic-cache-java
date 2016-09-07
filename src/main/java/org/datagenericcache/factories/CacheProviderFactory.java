package org.datagenericcache.factories;

import org.datagenericcache.models.ApplicationConfig;
import org.datagenericcache.providers.LocalMemoryProvider;
import org.datagenericcache.providers.MongoDbProvider;
import org.datagenericcache.providers.RedisProvider;
import org.cfg4j.provider.ConfigurationProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CacheProviderFactory {

    private static ApplicationConfig applicationConfig;
    private static HashMap<String, Class> providerClasses;

    public CacheProviderFactory() {
        ConfigurationProviderFactory configurationProviderFactory = new ConfigurationProviderFactory();
        ConfigurationProvider configurationProvider = configurationProviderFactory.create();
        applicationConfig = configurationProvider.bind("data_generic_cache_providers", ApplicationConfig.class);
    }

    static {
        HashMap<String, Class> map = new HashMap<>();
        map.put("localmemory", LocalMemoryProvider.class);
        map.put("redis", RedisProvider.class);
        map.put("mongodb", MongoDbProvider.class);
        providerClasses = map;
    }

    public List<Class> create() {
        List<Class> providers = new ArrayList<>();

        applicationConfig
                .providers()
                .forEach(provider ->
                {
                    if (providerClasses.containsKey(provider)) {
                        Class providerClass = providerClasses.get(provider);
                        providers.add(providerClass);
                    }
                });

        return providers;
    }
}