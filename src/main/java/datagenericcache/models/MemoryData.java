package datagenericcache.models;

import java.time.Duration;
import java.time.Instant;

public class MemoryData<T> {

    private Instant timeToExpire;
    private T value;

    public MemoryData(T value) {
        this(value, null);
    }

    public MemoryData(T value, Duration duration) {
        this.value = value;

        if (duration != null) {
            this.timeToExpire = Instant.now().plus(duration);
        }
    }

    public T getValue() {
        return value;
    }

    public boolean isExpired() {
        return timeToExpire != null && Instant.now().compareTo(timeToExpire) > 0;
    }
}