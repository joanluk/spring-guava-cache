package com.olisoft.spring.cache.guava;


import org.junit.Assert;
import org.junit.Test;
import org.springframework.cache.Cache;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * @author jose luque
 * @see 1.0
 */
public class GuavaCacheFactoryBeanTest {

    @Test
    public void testDefaultConfig() throws Exception {
        GuavaCacheFactoryBean guavaCacheFactoryBean = new GuavaCacheFactoryBean();
        Assert.assertTrue(guavaCacheFactoryBean.isSingleton());
        guavaCacheFactoryBean.afterPropertiesSet();
        Cache cache = guavaCacheFactoryBean.getObject();
        cache.put("key1", "element1");
        cache.put("key2", "element2");
        Assert.assertEquals(cache.getName(), "");
        Assert.assertEquals(cache.get("key1").get(), "element1");
        cache.put("key3", null);
        Assert.assertEquals(cache.get("key3").get(), NullValue.INSTANCE);

    }

    @Test
    public void testCustomConfig() throws Exception {
        GuavaCacheFactoryBean factoryBean = new GuavaCacheFactoryBean();
        factoryBean.setBeanName("cacheName");
        factoryBean.setAllowNullValues(true);
        factoryBean.setSpec("maximumSize=3");
        factoryBean.afterPropertiesSet();
        GuavaCache cache = factoryBean.getObject();

        assertThat(cache.getName()).isEqualTo("cacheName");
        assertThat(cache.isAllowNullValues()).isTrue();

        // spec
        cache.put("key1", "value1");
        cache.put("key2", "value2");
        cache.put("key3", "value3");
        assertThat(((com.google.common.cache.Cache)(cache.getNativeCache())).size()).isEqualTo(3);


        // allow null
        cache.put("key", null);
        assertThat(cache.get("key").get()).isEqualsToByComparingFields(NullValue.INSTANCE);
    }

    @Test
    public void testSingleton() throws Exception {
        GuavaCacheFactoryBean factoryBean = new GuavaCacheFactoryBean();
        factoryBean.afterPropertiesSet();
        GuavaCache cache1 = factoryBean.getObject();
        GuavaCache cache2 = factoryBean.getObject();

        assertThat(factoryBean.isSingleton()).isTrue();
        assertThat(cache1).isSameAs(cache2);
    }

}