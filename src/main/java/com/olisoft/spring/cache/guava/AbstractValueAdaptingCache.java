package com.olisoft.spring.cache.guava;

import org.springframework.cache.Cache;
import org.springframework.cache.support.SimpleValueWrapper;


/**
 * Common base class for {@link Cache} implementations that need to adapt
 * {@code null} values (and potentially other such special values) before
 * passing them on to the underlying store.
 *
 * <p>Transparently replaces given {@code null} user values with an internal
 * {@link NullValue#INSTANCE}, if configured to support {@code null} values
 * (as indicated by {@link #isAllowNullValues()}.
 *
 * @since 1.0
 */
public abstract class AbstractValueAdaptingCache implements Cache {

    private final boolean allowNullValues;


    /**
     * Create an {@code AbstractValueAdaptingCache} with the given setting.
     * @param allowNullValues whether to allow for {@code null} values
     */
    protected AbstractValueAdaptingCache(boolean allowNullValues) {
        this.allowNullValues = allowNullValues;
    }


    /**
     * Return whether {@code null} values are allowed in this cache.
     */
    public final boolean isAllowNullValues() {
        return this.allowNullValues;
    }

    @Override
    public ValueWrapper get(Object key) {
        Object value = lookup(key);
        return toValueWrapper(value);
    }

    public abstract Object lookup(Object key);

    /**
     * Convert the given value from the internal store to a user value
     * returned from the get method (adapting {@code null}).
     * @param storeValue the store value
     * @return the value to return to the user
     */
    protected Object fromStoreValue(Object storeValue) {
        if (this.allowNullValues && storeValue == NullValue.INSTANCE) {
            return null;
        }
        return storeValue;
    }

    /**
     * Convert the given user value, as passed into the put method,
     * to a value in the internal store (adapting {@code null}).
     * @param userValue the given user value
     * @return the value to store
     */
    protected Object toStoreValue(Object userValue) {
        if (this.allowNullValues && userValue == null) {
            return NullValue.INSTANCE;
        }
        return userValue;
    }

    /**
     * Wrap the given store value with a {@link SimpleValueWrapper}, also going
     * through {@link #fromStoreValue} conversion. Useful for {@link #get(Object)}
     * and {@link #putIfAbsent(Object, Object)} implementations.
     * @param storeValue the original value
     * @return the wrapped value
     */
    protected Cache.ValueWrapper toValueWrapper(Object storeValue) {
        return (storeValue != null ? new SimpleValueWrapper(fromStoreValue(storeValue)) : null);
    }
}
