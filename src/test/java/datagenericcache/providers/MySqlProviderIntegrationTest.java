package datagenericcache.providers;

import org.junit.*;
import org.testcontainers.containers.GenericContainer;

import java.sql.SQLException;
import java.time.Duration;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.Assert.*;

public class MySqlProviderIntegrationTest {
    @ClassRule
    public static GenericContainer mysqlContainer = new GenericContainer("mysql:latest").withExposedPorts(3307);
    
    private CacheProvider cacheProvider;

    private final String key = "key";
    private final String value = "value";

    @Before
    public void before() throws SQLException, ClassNotFoundException {
        if (mysqlContainer.isRunning()) {
            String host = mysqlContainer.getContainerIpAddress();
            String portNumber = mysqlContainer.getMappedPort(3307).toString();

            /*
            String jdbcConnection = String.format("jdbc:mysql//%s:%s", host, portNumber);
            cacheProvider = new MySqlProvider(jdbcConnection);
            */
        }
    }

    @Ignore("must be fixed")
    @Test
    public void shouldRetrieveNullWhenValueDoesntExistInMySqlLiveProvider() {
        String obtained = cacheProvider.retrieve("whenValueDoesntExistKey");

        assertNull(obtained);
    }

    @Ignore("must be fixed")
    @Test
    public void shouldAddAndRetrieveSuccessfullInMySqlLiveProvider() {
        cacheProvider.add(key, value, Duration.ofSeconds(1));

        assertEquals(value, cacheProvider.retrieve(key));
    }

    @Ignore("must be fixed")
    @Test
    public void shouldAddWhenInvokingRetrieveOrElseInMySqlLiveProvider() {
        cacheProvider.retrieveOrElse(key, Duration.ofSeconds(1), () -> value);

        assertEquals(value, cacheProvider.retrieve(key));
    }

    @Ignore("must be fixed")
    @Test
    public void shouldExpireWhenAddWithTimeSpanInMySqlLiveProvider() {
        cacheProvider.add(key, value, Duration.ofMillis(50));

        await()
                .atMost(110, MILLISECONDS)
                .until(() -> cacheProvider.retrieve(key) == null);
    }

    @Ignore("must be fixed")
    @Test
    public void shouldCheckIfValueExistsInMySqlLiveProvider() {
        cacheProvider.add(key, value, Duration.ofSeconds(1));

        assertTrue(cacheProvider.exists(key));
    }

    @Ignore("must be fixed")
    @Test
    public void shouldRemoveValueInMySqlLiveProvider() {
        cacheProvider.add(key, value, Duration.ofSeconds(1));
        cacheProvider.remove(key);

        assertFalse(cacheProvider.exists(key));
    }
}