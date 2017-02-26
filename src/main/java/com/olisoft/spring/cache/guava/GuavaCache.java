package com.olisoft.spring.cache.guava;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheBuilderSpec;
import org.springframework.cache.support.SimpleValueWrapper;
import org.springframework.util.Assert;

import java.io.Serializable;

/**
 * Spring {@link org.springframework.cache.Cache} adapter implementation
 * on top of a Guava {@link com.google.common.cache.Cache} instance.
 .
 * @author jose luque
 * @since 1.0
 */
public class GuavaCache extends AbstractValueAdaptingCache {

    private static final Object NULL_HOLDER = new NullHolder();

    private final String name;

    private final com.google.common.cache.Cache<Object, Object> guavaCache;

    private final boolean allowNullValues;

    /**
     * Create a new GuavaCache with the specified name.
     * @param name the name of the cache
     */
    public GuavaCache(String name) {
        this(name, CacheBuilder.newBuilder(), true);
    }

    /**
     * Create a new GuavaCache with the specified name.
     * @param name the name of the cache
     * @param allowNullValues whether to accept and convert null values for this cache
     */
    public GuavaCache(String name, boolean allowNullValues) {
        this(name, CacheBuilder.newBuilder(), allowNullValues);
    }

    /**
     * Create a new GuavaCache using the specified name and {@link CacheBuilderSpec specification}
     * @param name the name of the cache
     * @param spec the cache builder specification to use to build he cache
     */
    public GuavaCache(String name, CacheBuilderSpec spec, boolean allowNullValues) {

        this(name, CacheBuilder.from(spec), allowNullValues);
    }

    /**
     * Create a new GuavaCache using the specified name and {@link CacheBuilderSpec specification}
     * @param name the name of the cache
     * @param builder the cache builder to use to build the cache
     */
    public GuavaCache(String name, CacheBuilder<Object, Object> builder, boolean allowNullValues) {
        super(allowNullValues);
        Assert.notNull(name, "name is required");
        Assert.notNull(builder, "cache builder is required");
        this.name = name;
        this.allowNullValues = allowNullValues;
        this.guavaCache = builder.build();
    }

    public GuavaCache(String name, com.google.common.cache.Cache<Object, Object> cache, boolean allowNullValues) {
        super(allowNullValues);
        Assert.notNull(name, "name is required");
        Assert.notNull(cache, "cache is required");
        this.name = name;
        this.allowNullValues = allowNullValues;
        this.guavaCache = cache;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Object getNativeCache() {
        return this.guavaCache;
    }

    @Override
    public ValueWrapper get(Object key) {
        Object value = this.guavaCache.getIfPresent(key);
        return (value != null ? new SimpleValueWrapper(fromStoreValue(value)) : null);
    }

    @Override
    public Object lookup(Object key) {
        return this.guavaCache.getIfPresent(key);
    }

    @Override
    public void put(Object key, Object value) {
        this.guavaCache.put(key, toStoreValue(value));
    }

    @Override
    public void evict(Object key) {
       this.guavaCache.invalidate(key);
    }

    @Override
    public void clear() {
         this.guavaCache.invalidateAll();;
    }

    /**
     * Convert the given value from the internal store to a user value
     * returned from the get method (adapting {@code null}).
     * @param storeValue the store value
     * @return the value to return to the user
     */
    protected Object fromStoreValue(Object storeValue) {
        if (this.allowNullValues && storeValue == NULL_HOLDER) {
            return null;
        }
        return storeValue;
    }

    @SuppressWarnings("serial")
    private static class NullHolder implements Serializable {

    }


}
