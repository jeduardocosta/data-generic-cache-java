package org.datagenericcache.providers;

import com.alibaba.fastjson.JSON;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import redis.clients.jedis.Jedis;

import java.time.Duration;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

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
    public void shouldUpdateValueWhenInvokingSetInRedisProvider() {
        cacheProvider.set(key, "value", Duration.ofSeconds(1));

        verify(redisMock, times(1)).del(key.toLowerCase());
        verify(redisMock, times(1)).set(key.toLowerCase(), JSON.toJSONString("value"));
        verify(redisMock, times(1)).expire(key.toLowerCase(), (int)Duration.ofSeconds(1).getSeconds());
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

    @Test
    public void shouldReturnNullWhenCallbackFunctionDoesNotReturnResult() {
        when(redisMock.get(key.toLowerCase())).thenReturn(null);

        String obtained = cacheProvider.retrieveOrElse(key, Duration.ofSeconds(1), () -> null);

        assertNull(obtained);
    }

    @Test
    public void shouldNotAddValueWhenCallbackFunctionDoesNotReturnResult() {
        when(redisMock.get(key.toLowerCase())).thenReturn(null);

        cacheProvider.retrieveOrElse(key, Duration.ofSeconds(1), () -> null);

        verify(redisMock, never()).set(eq(key.toLowerCase()), any(String.class));
    }
}