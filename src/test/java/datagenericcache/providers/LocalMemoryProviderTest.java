package datagenericcache.providers;

import static org.junit.Assert.assertEquals;

import java.time.Duration;
import org.junit.*;

public class LocalMemoryProviderTest {

	private CacheProvider cacheProvider;

	@Before
	public void before() {
		cacheProvider = new LocalMemoryProvider();
	}

	@Test
	public void shouldRetrieveNullWhenValueDoesntExistInLocalMemoryCacher() {

		String obtained = this.cacheProvider.retrieve("ShouldRetrieveNullWhenValueDoesntExistKey");

		assertEquals(null, obtained);
	}

	@Test
	public void shouldAddAndRetrieveSuccessfullInLocalMemoryCacher() {

		final String key = "ShouldAddAndRetrieveSuccessfullyKey";
		final String value = "ShouldAddAndRetrieveSuccessfullyValue";

		this.cacheProvider.add(key, value, Duration.ofSeconds(1));

		String obtained = this.cacheProvider.retrieve(key);

		assertEquals(value, obtained);
	}

	@Test
	public void shouldAddWhenInvokingRetrieveOrElseInLocalMemoryCacher() {
		final String key = "ShouldAddWhenInvokingRetrieveOrElseKey";
		final String value = "ShouldAddWhenInvokingRetrieveOrElseValue";

		this.cacheProvider.retrieveOrElse(key, Duration.ofSeconds(1), value);

		String obtained = this.cacheProvider.retrieve(key);

		assertEquals(value, obtained);
	}

	@Test
	public void shouldExpireWhenAddWithTimeSpanInLocalMemoryCacher() throws InterruptedException {
		final String key = "ShouldExpireWhenAddWithTimespanKey";
		final String value = "ShouldExpireWhenAddWithTimespanValue";

		this.cacheProvider.add(key, value, Duration.ofMillis(1));

		Thread.sleep(10);

		String obtained = this.cacheProvider.retrieve(key);

		assertEquals(null, obtained);
	}

	@Test
	public void shouldCheckIfValueExistsInLocalMemoryCacher() {
		final String key = "ShouldCheckIfValueExistsInLocalMemoryCacher";
		final String value = "ShouldCheckIfValueExistsInLocalMemoryCacher";

		this.cacheProvider.add(key, value, Duration.ofSeconds(1));

		boolean obtained = this.cacheProvider.exists(key);

		assertEquals(true, obtained);
	}

	@Test
	public void shouldRemoveValueInLocalMemoryCacher() {
		final String key = "ShouldRemoveValueInLocalMemoryCacher";
		final String value = "ShouldRemoveValueInLocalMemoryCacher";

		this.cacheProvider.add(key, value, Duration.ofSeconds(1));
		this.cacheProvider.remove(key);

		boolean obtained = this.cacheProvider.exists(key);

		assertEquals(false, obtained);
	}
}