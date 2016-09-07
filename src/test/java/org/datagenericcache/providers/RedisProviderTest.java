package org.datagenericcache.providers;

import com.alibaba.fastjson.JSON;
import org.datagenericcache.providers.CacheProvider;
import org.datagenericcache.providers.RedisProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import redis.clients.jedis.Jedis;

import java.time.Duration;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RedisProviderTest {
    private final String key = "key";
    private final String value = "value";

    @Mock
    private Jedis redisMock;

    private CacheProvider cacheProvider;

    @Before
    public void before() {
        cacheProvider = new RedisProvider(redisMock);
    }

    @Test
    public void shouldRetrieveNullWhenValueDoesntExistInRedisProvider() {
        String key = "whenValueDoesntExistKey";

        when(redisMock.get(key.toLowerCase())).thenReturn(null);

        assertNull(cacheProvider.retrieve(key));
    }

    @Test
    public void shouldAddAndRetrieveSuccessfullInRedisProvider() {
        String valueAsJson = JSON.toJSONString(value);

        when(redisMock.get(key.toLowerCase())).thenReturn(valueAsJson);

        cacheProvider.add(key, value, Duration.ofSeconds(1));

        assertEquals(value, cacheProvider.retrieve(key));
    }

    @Test
    public void shouldAddWhenInvokingRetrieveOrElseInRedisProvider() {
        String valueAsJson = JSON.toJSONString(value);

        when(redisMock.get(key.toLowerCase())).thenReturn(valueAsJson);

        cacheProvider.retrieveOrElse(key, Duration.ofSeconds(1), () -> value);

        assertEquals(value, cacheProvider.retrieve(key));
    }

    @Test
    public void shouldRetrieveValueWhenInvokingRetrieveOrElseInRedisProvider() {
        String obtained = cacheProvider.retrieveOrElse(key, Duration.ofSeconds(1), () -> value);

        assertEquals(value, obtained);
    }

    @Test
    public void shouldCheckIfValueExistsInRedisProvider() {
        when(redisMock.exists(key.toLowerCase())).thenReturn(true);

        cacheProvider.add(key, value, Duration.ofSeconds(1));

        assertTrue(cacheProvider.exists(key));
    }

    @Test
    public void shouldRemoveValueInRedisProvider() {
        when(redisMock.exists(key.toLowerCase())).thenReturn(false);

        cacheProvider.add(key, value, Duration.ofSeconds(1));
        cacheProvider.remove(key);

        assertFalse(cacheProvider.exists(key));
    }
}