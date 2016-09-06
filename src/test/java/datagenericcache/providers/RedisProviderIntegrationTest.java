package datagenericcache.providers;

import org.junit.*;
import org.testcontainers.containers.GenericContainer;

import java.time.Duration;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.Assert.*;

public class RedisProviderIntegrationTest {
    @ClassRule
    public static GenericContainer redisContainer= new GenericContainer("redis:3.0.7").withExposedPorts(6379);

    private CacheProvider cacheProvider;

    private final String key = "key";
    private final String value = "value";

    @Before
    public void before() {
        if (redisContainer.isRunning()) {
            String host = redisContainer.getContainerIpAddress();
            int portNumber = redisContainer.getMappedPort(6379);
            cacheProvider = new RedisProvider(host, portNumber);
        }
    }

    @Test
    public void shouldRetrieveNullWhenValueDoesntExistInRedisLiveProvider() {
        String obtained = cacheProvider.retrieve("whenValueDoesntExistKey");

        assertNull(obtained);
    }

    @Test
    public void shouldAddAndRetrieveSuccessfullInRedisLiveProvider() {
        cacheProvider.add(key, value, Duration.ofSeconds(1));

        assertEquals(value, cacheProvider.retrieve(key));
    }

    @Test
    public void shouldAddWhenInvokingRetrieveOrElseInRedisLiveProvider() {
        cacheProvider.retrieveOrElse(key, Duration.ofSeconds(1), () -> value);

        assertEquals(value, cacheProvider.retrieve(key));
    }

    @Test
    public void shouldExpireWhenAddWithTimeSpanInRedisLiveProvider() {
        cacheProvider.add(key, value, Duration.ofMillis(50));

        await()
                .atMost(110, MILLISECONDS)
                .until(() -> cacheProvider.retrieve(key) == null);
    }

    @Test
    public void shouldCheckIfValueExistsInRedisLiveProvider() {
        cacheProvider.add(key, value, Duration.ofSeconds(1));

        assertTrue(cacheProvider.exists(key));
    }

    @Test
    public void shouldRemoveValueInRedisLiveProvider() {
        cacheProvider.add(key, value, Duration.ofSeconds(1));
        cacheProvider.remove(key);

        assertFalse(cacheProvider.exists(key));
    }
}