package com.jeestudio.services.admin.dao.cacheFeign;

import com.jeestudio.common.component.FeignSupportConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @Description: Cache
 * @author: whl
 * @Date: 2019-12-03
 */
@FeignClient(value = "cache",configuration = FeignSupportConfig.class)
@Component
public interface CacheUserFeign {

    /**
     * Get object cache
     * @param key
     * @return Object to string
     */
    @GetMapping("/cache/getObjectCache")
    String getObject(@RequestParam("key") String key);

    /**
     * Delete object cache
     * @param key
     * @return "true" or "false"
     */
    @GetMapping("/cache/deleteObjectCache")
    String deleteObject(@RequestParam("key") String key);

    /**
     * Delete cache of matching key
     * @param key
     */
    @GetMapping("/cache/deleteLike")
    void deleteLike(@RequestParam("key") String key);

    /**
     * Set object and expired minute
     * @param key
     * @param value
     * @param time  expired minute
     * @return "true" of "false"
     */
    @PostMapping("/cache/setObjectCacheExpireMinute")
    String setObjectExpireMinute(@RequestParam("key") String key, @RequestParam("value") String value, @RequestParam("time") Long time);

    /**
     * Delete all cache
     * @return "true" of "false"
     */
    @GetMapping("/cache/deleteAllCache")
    String deleteAll();

    /**
     * Get cache size of matching key
     * @param key
     * @return size
     */
    @GetMapping("/cache/sizeLike")
    String sizeLike(@RequestParam("key") String key);

    /**
     * Get key set of matching key
     * @param key
     * @return key set
     */
    @GetMapping("/cache/keysLike")
    String keysLike(@RequestParam("key") String key);

    /**
     * Get value collection of matching key
     * @param key
     * @return value collection
     */
    @GetMapping("/cache/valuesLike")
    String valuesLike(@RequestParam("key") String key);

    /**
     * Determine whether the key exists
     * @param key
     * @return true of false
     */
    @GetMapping("/cache/existKey")
    Boolean exist(@RequestParam("key") String key);

    /**
     * Delete hash
     */
    @GetMapping("/cache/deleteHashCache")
    String deleteHash(@RequestParam("key") String key, @RequestParam("hashKey") String hashKey);

    /**
     * Delete hash of matching key
     */
    @GetMapping("/cache/deleteLikeHashCache")
    String deleteLikeHash(@RequestParam("key") String key, @RequestParam("hashKey") String hashKey);

}
