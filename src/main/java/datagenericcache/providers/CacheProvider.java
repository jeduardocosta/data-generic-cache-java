package datagenericcache.providers;

import java.time.Duration;

public interface CacheProvider {
	<T> void add(String key, T value, Duration duration);

	<T> void set(String key, T value, Duration duration);

	void remove(String key);

	boolean exists(String key);

	<T> T retrieve(String key);

	<T> T retrieveOrElse(String key, Duration duration, T newValue);
}