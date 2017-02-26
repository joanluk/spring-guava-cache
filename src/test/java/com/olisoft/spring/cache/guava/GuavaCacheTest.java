package com.olisoft.spring.cache.guava;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheBuilderSpec;
import org.junit.Test;

import java.util.concurrent.TimeUnit;
import static com.google.common.util.concurrent.Uninterruptibles.sleepUninterruptibly;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

public class GuavaCacheTest {

    @Test(expected = IllegalArgumentException.class)
    public void testNameIsRequired() {
        new GuavaCache(null);
    }

    @Test
    public void testNewWithSpec() {
        CacheBuilderSpec spec = CacheBuilderSpec.parse("maximumSize=2");
        GuavaCache cache = new GuavaCache("name", spec, true);
        cache.put("key1", "value1");
        cache.put("key2", "value2");
        cache.put("key3", "value3");

        assertThat(((Cache)cache.getNativeCache()).size()).isEqualTo(2);
    }

    @Test
    public void testGet() {
        GuavaCache cache = new GuavaCache("name");
        ((Cache)cache.getNativeCache()).put("key", "value");

        assertThat(cache.get("key").get()).isEqualTo("value");
    }

    @Test
    public void testGetAbsent() {
        GuavaCache cache = new GuavaCache("name");

        assertThat(cache.get("key")).isNull();
    }

    @Test
    public void testPut() {
        GuavaCache cache = new GuavaCache("name");
        cache.put("key", "value");

        assertThat(((Cache)cache.getNativeCache()).getIfPresent("key"))
                .isNotNull()
                .isEqualTo("value");
    }

    @Test
    public void testEvict() {
        GuavaCache cache = new GuavaCache("name");
        ((Cache)cache.getNativeCache()).put("key", "value");

        assertThat(((Cache)cache.getNativeCache()).getIfPresent("key"))
                .isNotNull()
                .isEqualTo("value");

        cache.evict("key");
        assertThat(((Cache)cache.getNativeCache()).getIfPresent("key")).isNull();
    }

    @Test
    public void testClear() {
        GuavaCache cache = new GuavaCache("name");
        ((Cache)cache.getNativeCache()).put("key1", "value1");
        ((Cache)cache.getNativeCache()).put("key2", "value2");
        ((Cache)cache.getNativeCache()).put("key3", "value3");

        assertThat(((Cache)cache.getNativeCache()).size()).isEqualTo(3);

        cache.clear();
        assertThat(((Cache)cache.getNativeCache()).size()).isZero();
    }

    @Test
    public void testAllowNullValues() {
        GuavaCache cache = new GuavaCache("name", true);
        cache.put("key", null);
        assertThat(cache.get("key").get()).isEqualTo(NullValue.INSTANCE);
    }

    @Test(expected = NullPointerException.class)
    public void testDisallowNullValues() {
        GuavaCache cache = new GuavaCache("name", false);

        ((Cache)cache.getNativeCache()).put("key", null);
    }

    @Test
    public void testExpire() {
        CacheBuilder<Object, Object> builder = CacheBuilder.newBuilder().expireAfterWrite(2, TimeUnit.SECONDS);
        GuavaCache cache = new GuavaCache("name", builder, false);
        ((Cache)cache.getNativeCache()).put("key", "value");
        assertEquals("value", cache.get("key").get());

        // wait for expiration
        sleepUninterruptibly(3, TimeUnit.SECONDS);

        assertThat(cache.get("key")).isNull();
    }

}