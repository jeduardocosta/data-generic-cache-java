package datagenericcache.providers;

import org.junit.*;
import org.testcontainers.containers.GenericContainer;

import java.time.Duration;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.Assert.*;

public class MongoDbProviderIntegrationTest {
    @ClassRule
    public static GenericContainer container = new GenericContainer("mongo:3.2.9").withExposedPorts(27017);
    
    private CacheProvider cacheProvider;

    private final String key = "key";
    private final String value = "value";

    @Before
    public void before() {
        if (container.isRunning()) {
            String host = container.getContainerIpAddress();
            int portNumber = container.getMappedPort(27017);
            cacheProvider = new MongoDbProvider(host, portNumber);
        }
    }

    @After
    public void after() {
        cacheProvider.flush();
    }

    @Test
    public void shouldRetrieveNullWhenValueDoesntExistInMongoDbLiveProvider() {
        String obtained = cacheProvider.retrieve("whenValueDoesntExistKey");

        assertNull(obtained);
    }

    @Test
    public void shouldAddAndRetrieveSuccessfullInMongoDbLiveProvider() {
        cacheProvider.add(key, value, Duration.ofSeconds(1));

        assertEquals(value, cacheProvider.retrieve(key));
    }

    @Test
    public void shouldAddWhenInvokingRetrieveOrElseInMongoDbLiveProvider() {
        cacheProvider.retrieveOrElse(key, Duration.ofSeconds(1), () -> value);

        assertEquals(value, cacheProvider.retrieve(key));
    }

    @Test
    public void shouldExpireWhenAddWithTimeSpanInMongoDbLiveProvider() {
        cacheProvider.add(key, value, Duration.ofMillis(50));

        await()
                .atMost(150, MILLISECONDS)
                .until(() -> cacheProvider.retrieve(key) == null);
    }

    @Test
    public void shouldCheckIfValueExistsInMongoDbLiveProvider() {
        cacheProvider.add(key, value, Duration.ofSeconds(1));

        assertTrue(cacheProvider.exists(key));
    }

    @Test
    public void shouldRemoveValueInMongoDbLiveProvider() {
        cacheProvider.add(key, value, Duration.ofSeconds(1));
        cacheProvider.remove(key);

        assertFalse(cacheProvider.exists(key));
    }
}