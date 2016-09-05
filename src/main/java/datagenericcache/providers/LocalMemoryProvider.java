package datagenericcache.providers;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

public class LocalMemoryProvider implements CacheProvider {

	private static Map<String, Object> cache;

	static {
		cache = new ConcurrentHashMap<String, Object>();
	}

	@Override
	public <T> void add(String key, T value, Duration duration) {
		MemoryData<T> data = new MemoryData<T>(value, duration);
		cache.put(key.toLowerCase(), data);
	}

	@Override
	public <T> void set(String key, T value, Duration duration) {
		remove(key);
		add(key, value, duration);
	}

	@Override
	public void remove(String key) {
		cache.remove(key.toLowerCase());
	}

	@Override
	public boolean exists(String key) {
		return cache.get(key.toLowerCase()) != null;
	}

	@Override
	public <T> T retrieve(String key) {
		Object retrieved = cache.get(key.toLowerCase());

		if (retrieved == null) {
			return null;
		}

		MemoryData<Object> memoryData = (retrieved instanceof MemoryData ? (MemoryData<Object>) retrieved : null);

		if (memoryData == null) {
			return null;
		}

		if (memoryData.isExpired()) {
			remove(key);
			return null;
		}

		T value = (T) memoryData.getValue();
		return value;

	}

	@Override
	public <T> T retrieveOrElse(String key, Duration duration, Callable<T> retrieveFunction) {

		T cachedObject = retrieve(key);

		if (cachedObject == null) {

			T retrievedObject = null;

			try {
				retrievedObject = retrieveFunction.call();
			} catch (Exception exception) {

			}

			if (retrievedObject == null) {
				return null;
			}

			add(key, retrievedObject, duration);
			cachedObject = retrievedObject;
		}

		return cachedObject;
	}
}
