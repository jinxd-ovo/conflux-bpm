package com.jeestudio.services.authorization.CacheManager;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.cache.CacheManager;

/**
 * @Description: Redis cache manager
 * @author: whl
 * @Date: 2019-12-03
 */
public class RedisCacheManager implements CacheManager {
    @Override
    public <K, V> Cache<K, V> getCache(String s) throws CacheException {
        return new RedisCache<K, V>();
    }
}
