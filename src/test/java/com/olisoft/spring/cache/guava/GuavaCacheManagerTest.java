package com.olisoft.spring.cache.guava;

import org.junit.Test;
import org.springframework.cache.Cache;

import static org.fest.assertions.api.Assertions.assertThat;

public class GuavaCacheManagerTest {

    @Test
    public void testCacheManagerDefault() {
        GuavaCacheManager guavaCacheManager = new GuavaCacheManager();
        Cache cache = guavaCacheManager.getCache("test");
        assertThat(guavaCacheManager.getCacheNames().size()).isEqualTo(1);

    }
    @Test
    public void testCacheManagerCustom() {
        GuavaCacheManager guavaCacheManager = new GuavaCacheManager("createCache");
        assertThat(guavaCacheManager.getCacheNames().size()).isEqualTo(1);

    }


}