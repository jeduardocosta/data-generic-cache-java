package datagenericcache.providers;

import java.time.Duration;

import org.junit.*;

import static org.awaitility.Awaitility.*;
import static java.util.concurrent.TimeUnit.*;
import static org.junit.Assert.*;

public class LocalMemoryProviderTest {
    private CacheProvider cacheProvider;

    private final String key = "key";
    private final String value = "value";

    @Before
    public void before() {
        cacheProvider = new LocalMemoryProvider();
    }

    @Test
    public void shouldRetrieveNullWhenValueDoesntExistInLocalMemoryCacher() {
        String obtained = cacheProvider.retrieve("whenValueDoesntExistKey");

        assertNull(obtained);
    }

    @Test
    public void shouldAddAndRetrieveSuccessfullInLocalMemoryCacher() {
        cacheProvider.add(key, value, Duration.ofSeconds(1));

        assertEquals(value, cacheProvider.retrieve(key));
    }

    @Test
    public void shouldAddWhenInvokingRetrieveOrElseInLocalMemoryCacher() {
        cacheProvider.retrieveOrElse(key, Duration.ofSeconds(1), () -> value);

        assertEquals(value, cacheProvider.retrieve(key));
    }

    @Test
    public void shouldExpireWhenAddWithTimeSpanInLocalMemoryCacher() {
        cacheProvider.add(key, value, Duration.ofMillis(50));

        await()
                .atMost(110, MILLISECONDS)
                .until(() -> cacheProvider.retrieve(key) == null);
    }

    @Test
    public void shouldCheckIfValueExistsInLocalMemoryCacher() {
        cacheProvider.add(key, value, Duration.ofSeconds(1));

        assertTrue(cacheProvider.exists(key));
    }

    @Test
    public void shouldRemoveValueInLocalMemoryCacher() {
        cacheProvider.add(key, value, Duration.ofSeconds(1));
        cacheProvider.remove(key);

        assertFalse(cacheProvider.exists(key));
    }
}