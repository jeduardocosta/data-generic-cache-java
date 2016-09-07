package org.datagenericcache.providers;

import org.datagenericcache.providers.CacheProvider;
import org.junit.After;
import org.junit.Test;

import java.time.Duration;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.Assert.*;

public abstract class CacheProviderIntegrationTest {
    protected CacheProvider cacheProvider;

    private final String key = "key";
    private final String value = "value";

    @After
    public void after() {
        cacheProvider.flush();
    }

    @Test
    public void shouldRetrieveNullWhenValueDoesntExistInCacheLiveProvider() {
        String obtained = cacheProvider.retrieve("whenValueDoesntExistKey");

        assertNull(obtained);
    }

    @Test
    public void shouldAddAndRetrieveSuccessfullInCacheLiveProvider() {
        cacheProvider.add(key, value, Duration.ofSeconds(1));

        assertEquals(value, cacheProvider.retrieve(key));
    }

    @Test
    public void shouldAddWhenInvokingRetrieveOrElseInCacheLiveProvider() {
        cacheProvider.retrieveOrElse(key, Duration.ofSeconds(1), () -> value);

        assertEquals(value, cacheProvider.retrieve(key));
    }

    @Test
    public void shouldExpireWhenAddWithTimeSpanInCacheLiveProvider() {
        cacheProvider.add(key, value, Duration.ofMillis(50));

        await()
                .atMost(150, MILLISECONDS)
                .until(() -> cacheProvider.retrieve(key) == null);
    }

    @Test
    public void shouldCheckIfValueExistsInCacheLiveProvider() {
        cacheProvider.add(key, value, Duration.ofSeconds(1));

        assertTrue(cacheProvider.exists(key));
    }

    @Test
    public void shouldRemoveValueInCacheLiveProvider() {
        cacheProvider.add(key, value, Duration.ofSeconds(1));
        cacheProvider.remove(key);

        assertFalse(cacheProvider.exists(key));
    }
}