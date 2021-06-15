package com.github.m5rian.myra.utilities;

import com.mongodb.lang.Nullable;

import java.util.concurrent.*;
import java.util.function.Function;

/**
 * This Class can store a pair in the memory cache using a {@link ConcurrentMap}
 *
 * @param <K> Object type of keys.
 * @param <V> Object type of values.
 * @author Marian
 */
public class Cache<K, V> {
    /**
     * A {@link ConcurrentMap} which contains {@link K} as the key
     * and {@link V} as the value.
     */
    private final ConcurrentMap<K, V> cache = new ConcurrentHashMap<>();
    /**
     * This Map stores the timeout times, which remove the items from {@link Cache#cache}.
     * <p>
     * A {@link ConcurrentMap} which contains {@link K} as the key
     * and a {@link ScheduledFuture} as the value.
     */
    private final ConcurrentMap<K, ScheduledFuture<?>> cacheSchedulers = new ConcurrentHashMap<>();

    /**
     * The {@link ScheduledExecutorService} to wait for the timeout.
     */
    private final ScheduledExecutorService timer = Executors.newScheduledThreadPool(5);
    private Function<K, V> loadFunction;
    private Long timeout;
    private TimeUnit timeunit = TimeUnit.SECONDS;

    public Cache<K, V> setLoadFunction(Function<K, V> function) {
        this.loadFunction = function;
        return this;
    }

    /**
     * Add an item to the cache.
     *
     * @param key   The key of the pair.
     * @param value The value of the pair.
     * @return Returns the current {@link Cache} object for chaining.
     */
    public Cache<K, V> add(K key, V value) {
        this.cache.put(key, value);

        if (this.timeout != null) {
            final ScheduledFuture<V> scheduler = timer.schedule(() -> this.cache.remove(key), this.timeout, this.timeunit); // Create timer
            this.cacheSchedulers.put(key, scheduler); // Add timer to list
        }

        return this;
    }

    /**
     * If no key was found this method can return null.
     *
     * @param key The key to search for in the {@link Cache#cache}
     * @return Returns the value of the specified key.
     */
    @Nullable
    public V get(K key) {
        // Item isn't cached yet
        if (!this.cache.containsKey(key)) {
            final V value = this.loadFunction.apply(key); // Get value
            this.cache.put(key, value); // Put pair in cache
        }

        if (this.timeout != null) {
            // There is already a running timer for the timeout
            if (this.cacheSchedulers.containsKey(key)) {
                this.cacheSchedulers.get(key).cancel(false); // Stop current timer
            }
            final ScheduledFuture<?> scheduler = timer.schedule(() -> {
                this.cache.remove(key);
            }, this.timeout, this.timeunit);// Create new timer
            this.cacheSchedulers.put(key, scheduler); // Add timer to list
        }
        return cache.getOrDefault(key, null);
    }

    /**
     * @param timeout The duration until the timeout fires in milliseconds.
     * @return Returns the current {@link Cache} object for chaining.
     */
    public Cache<K, V> setTimeout(Long timeout) {
        this.timeout = timeout;
        return this;
    }

    /**
     * @param timeout  The duration until the timeout fires.
     * @param timeunit Timeunit of the timeout.
     * @return Returns the current {@link Cache} object for chaining.
     */
    public Cache<K, V> setTimeout(Long timeout, TimeUnit timeunit) {
        this.timeout = timeout;
        this.timeunit = timeunit;
        return this;
    }
}
