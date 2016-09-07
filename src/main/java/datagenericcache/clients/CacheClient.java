package datagenericcache.clients;

import datagenericcache.factories.CacheClientFactory;
import datagenericcache.factories.CacheProviderFactory;
import datagenericcache.providers.CacheProvider;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.Callable;

public class CacheClient implements CacheProvider {
    private static List<Class> availableProviders;

    private CacheProvider cacheProvider;
    private CacheClientFactory cacheClientFactory;

    public CacheClient() {
        this(new CacheClientFactory());
    }

    public CacheClient(CacheClientFactory factory) {
        cacheClientFactory = factory;
        cacheProvider = cacheClientFactory.create(availableProviders);
    }

    static {
        CacheProviderFactory cacheProviderFactory = new CacheProviderFactory();
        availableProviders = cacheProviderFactory.create();
    }

    @Override
    public <T> void add(String key, T value, Duration duration) {
        doProcess(() -> cacheProvider.add(key, value, duration));
    }

    @Override
    public <T> void set(String key, T value, Duration duration) {
        doProcess(() -> cacheProvider.set(key, value, duration));
    }

    @Override
    public void remove(String key) {
        doProcess(() -> cacheProvider.remove(key));
    }

    @Override
    public boolean exists(String key) {
        return doProcess(() -> cacheProvider.exists(key));
    }

    @Override
    public <T> T retrieve(String key) {
        return doProcess(() -> cacheProvider.retrieve(key));
    }

    @Override
    public <T> T retrieveOrElse(String key, Duration duration, Callable<T> retrieveFunction) {
        return doProcess(() -> cacheProvider.retrieveOrElse(key, duration, retrieveFunction));
    }

    @Override
    public void flush() {
        doProcess(() -> cacheProvider.flush());
    }

    private <T> T doProcess(Callable<T> callback) {
        T result = null;

        try {
            result = callback.call();
        }
        catch (Exception exception) {
            cacheProvider = cacheClientFactory.create(availableProviders);
            doProcess(callback);
        }

        return result;
    }

    private void doProcess(Runnable callback) {
        doProcess(() ->
        {
            callback.run();
            return true;
        });
    }
}