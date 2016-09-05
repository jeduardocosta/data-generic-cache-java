package datagenericcache.providers;

import static org.junit.Assert.assertEquals;

import java.time.Duration;
import org.junit.*;

public class LocalMemoryProviderTest {

	private CacheProvider cacheProvider;

	final String key = "key";
	final String value = "value";

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
		cacheProvider.retrieveOrElse(key, Duration.ofSeconds(1), value);

		String obtained = cacheProvider.retrieve(key);

		assertEquals(value, obtained);
	}

	@Test
	public void shouldExpireWhenAddWithTimeSpanInLocalMemoryCacher() throws InterruptedException {
		cacheProvider.add(key, value, Duration.ofMillis(1));

		Thread.sleep(10);

		String obtained = cacheProvider.retrieve(key);

		assertEquals(null, obtained);
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