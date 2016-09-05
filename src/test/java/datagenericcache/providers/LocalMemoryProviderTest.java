package datagenericcache.providers;

import static org.junit.Assert.assertEquals;

import java.time.Duration;
import org.junit.*;

import static org.awaitility.Awaitility.*;
import static java.util.concurrent.TimeUnit.*;

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

		assertEquals(null, obtained);
	}

	@Test
	public void shouldAddAndRetrieveSuccessfullInLocalMemoryCacher() {
		cacheProvider.add(key, value, Duration.ofSeconds(1));

		String obtained = cacheProvider.retrieve(key);

		assertEquals(value, obtained);
	}

	@Test
	public void shouldAddWhenInvokingRetrieveOrElseInLocalMemoryCacher() {
		cacheProvider.retrieveOrElse(key, Duration.ofSeconds(1), () -> value);

		String obtained = cacheProvider.retrieve(key);

		assertEquals(value, obtained);
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

		boolean obtained = cacheProvider.exists(key);

		assertEquals(true, obtained);
	}

	@Test
	public void shouldRemoveValueInLocalMemoryCacher() {
		cacheProvider.add(key, value, Duration.ofSeconds(1));
		cacheProvider.remove(key);

		boolean obtained = cacheProvider.exists(key);

		assertEquals(false, obtained);
	}
}