package org.datagenericcache.providers;

import com.alibaba.fastjson.JSON;
import org.datagenericcache.models.ProviderConfig;
import org.datagenericcache.models.ProviderConfigImpl;
import redis.clients.jedis.Jedis;

import java.time.Duration;
import java.util.concurrent.Callable;

public class RedisProvider implements CacheProvider {
    private Jedis redis;

    public RedisProvider() {
        ProviderConfig providerConfig = new ProviderConfigImpl("data_generic_cache_redis");
        this.redis = new Jedis(providerConfig.host(), providerConfig.portNumber());
    }

    public RedisProvider(String host, int portNumber) {
        this(new Jedis(host, portNumber));
    }

    public RedisProvider(Jedis redis) {
        this.redis = redis;
    }

    @Override
    public <T> void add(String key, T value, Duration duration) {
        String json = JSON.toJSONString(value);

        redis.set(key.toLowerCase(), json);

        if (duration != null) {
            redis.expire(key.toLowerCase(), (int) duration.getSeconds());
        }
    }

    @Override
    public <T> void set(String key, T value, Duration duration) {
        remove(key);
        add(key, value, duration);
    }

    @Override
    public void remove(String key) {
        redis.del(key.toLowerCase());
    }

    @Override
    public boolean exists(String key) {
        return redis.exists(key.toLowerCase());
    }

    @Override
    public <T> T retrieve(String key) {
        String json = redis.get(key.toLowerCase());

        if (json == null) {
            return null;
        }

        T result = null;

        try {
            result = (T) JSON.parse(json);
        } catch (Exception exception) {
        }

        return result;
    }

    @Override
    public <T> T retrieveOrElse(String key, Duration duration, Callable<T> retrieveFunction) {
        T cachedObject = retrieve(key);

        if (cachedObject == null) {
            T retrievedObject = null;

            try {
                retrievedObject = retrieveFunction.call();
            } catch (Exception exception) {
            }

            if (retrievedObject == null) {
                return null;
            }

            add(key, retrievedObject, duration);
            cachedObject = retrievedObject;
        }

        return cachedObject;
    }

    @Override
    public void flush() {
        redis.flushAll();
    }
}