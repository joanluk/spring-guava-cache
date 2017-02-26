package com.olisoft.spring.cache.guava;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * {@link CacheManager} implementation that lazily builds {@link GuavaCache}
 * instances for each {@link #getCache} request.
 *
 * <p>The configuration of the underlying cache can be fine-tuned through a
 * Guava {@link CacheBuilder} or {@link CacheBuilderSpec}, passed into this
 * CacheManager through {@link #setCacheBuilder}/{@link #setCacheBuilderSpec}.
 * A {@link CacheBuilderSpec}-compliant expression value can also be applied
 * via the {@link #setCacheSpecification "cacheSpecification"} bean property.
 *
 * <p>Requires Google Guava 12.0 or higher.
 *
 * @author jose luque
 * @since 1.0
 * @see GuavaCache
 */
public class GuavaCacheManager implements CacheManager {

    private final ConcurrentMap<String, Cache> cacheMap = new ConcurrentHashMap<String, Cache>(16);

    private boolean dynamic = true;

    private CacheBuilder<Object, Object> cacheBuilder = CacheBuilder.newBuilder();

    private CacheLoader<Object, Object> cacheLoader;

    private boolean allowNullValues = true;


    public GuavaCacheManager() {
    }

    public GuavaCacheManager(String... cacheNames) {
        setCacheNames(Arrays.asList(cacheNames));
    }


    @Override
    public Cache getCache(String name) {
        Cache cache = this.cacheMap.get(name);
        if (cache == null && this.dynamic) {
            synchronized (this.cacheMap) {
                cache = this.cacheMap.get(name);
                if (cache == null) {
                    cache = createGuavaCache(name);
                    this.cacheMap.put(name, cache);
                }
            }
        }
        return cache;
    }


    /**
     * Create a new GuavaCache instance for the specified cache name.
     * @param name the name of the cache
     * @return the Spring GuavaCache adapter (or a decorator thereof)
     */
    protected Cache createGuavaCache(String name) {
        return new GuavaCache(name, createNativeGuavaCache(name), isAllowNullValues());
    }

    /**
     * Create a native Guava Cache instance for the specified cache name.
     * @param name the name of the cache
     * @return the native Guava Cache instance
     */
    protected com.google.common.cache.Cache<Object, Object> createNativeGuavaCache(String name) {
        if (this.cacheLoader != null) {
            return this.cacheBuilder.build(this.cacheLoader);
        }
        else {
            return this.cacheBuilder.build();
        }
    }

    @Override
    public Collection<String> getCacheNames() {
        return Collections.unmodifiableSet(this.cacheMap.keySet());
    }

    public void setCacheNames(List<String> cacheNames) {
        if (cacheNames != null) {
            for (String name : cacheNames) {
                this.cacheMap.put(name, createGuavaCache(name));
            }
            this.dynamic = false;
        }
		else {
            this.dynamic = true;
        }

    }

    /**
     * Return whether this cache manager accepts and converts {@code null} values
     * for all of its caches.
     */
    public boolean isAllowNullValues() {
        return this.allowNullValues;
    }
}
