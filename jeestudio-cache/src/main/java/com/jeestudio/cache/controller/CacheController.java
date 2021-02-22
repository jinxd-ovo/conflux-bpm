package com.jeestudio.cache.controller;

import com.jeestudio.cache.cacheUtils.RedisUtil;
import com.jeestudio.utils.CacheData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * @Description: Cache Controller
 * @author: whl
 * @Date: 2019-11-30
 */
@Api(value = "CacheController ",tags = "Redis Cache")
@RestController
@RequestMapping("${cachePath}")
public class CacheController {

    private Logger logger = LoggerFactory.getLogger(CacheController.class);

    @Autowired
    private RedisUtil redisUtil;

    @Value("${cachePrefix}")
    private String cachePrefix;

    /**
     * Set object
     */
    @ApiOperation(value = "/setObjectCache", tags = "Save normal cache, as key-value")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "key", value = "key", required = true, dataType = "String"),
            @ApiImplicitParam(name = "value", value = "value", required = true, dataType = "String")
    })
    @PostMapping("/setObjectCache")
    public String setObject(@RequestParam("key") String key, @RequestParam("value") String value) {
        return redisUtil.setObject(cachePrefix + key, value);
    }

    /**
     * Get object
     */
    @ApiOperation(value = "/getObjectCache", tags = "Get normal cache")
    @ApiImplicitParams({@ApiImplicitParam(name = "key", value = "redis cache key", required = true, dataType = "String")})
    @GetMapping("/getObjectCache")
    public Object getObject(@RequestParam("key") String key) {
        try {
            return redisUtil.getObject(cachePrefix + key);
        } catch (Exception e) {
            logger.error("Error while getting object:" + e.getMessage());
            return "false";
        }
    }

    /**
     * Get object
     */
    @ApiOperation(value = "/getObjectAuthorization", tags = "Get normal cache")
    @ApiImplicitParams({@ApiImplicitParam(name = "key", value = "redis cache key", required = true, dataType = "String")})
    @GetMapping("/getObjectAuthorization")
    public String getObjectAuthorization(@RequestParam("key") String key) {
        try {
            Object obj = redisUtil.getObject(cachePrefix + key);
            if (obj != null) {
                return obj.toString();
            } else {
                return null;
            }
        } catch (Exception e) {
            logger.error("Error while getting object:" + e.getMessage());
            return null;
        }
    }

    /**
     * Delete object
     */
    @ApiOperation(value = "/deleteObjectCache", tags = "Delete normal cache")
    @ApiImplicitParams({@ApiImplicitParam(name = "key", value = "redis cache key", required = true, dataType = "String")})
    @GetMapping("/deleteObjectCache")
    public String deleteObject(@RequestParam("key") String key) {
        return redisUtil.deleteObject(cachePrefix + key);
    }

    /**
     * Set object and expired minute
     */
    @ApiOperation(value = "/setObjectCacheExpireMinute", tags = "Save normal cache and expire minute,as key-value")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "key", value = "key", required = true, dataType = "String"),
            @ApiImplicitParam(name = "value", value = "value", required = true, dataType = "String"),
            @ApiImplicitParam(name = "time", value = "time", required = true, dataType = "Long")
    })
    @PostMapping("/setObjectCacheExpireMinute")
    public String setObjectExpireMinute(@RequestParam("key") String key, @RequestParam("value") String value, @RequestParam("time") Long time) {
        return redisUtil.setObjectExpireMinute(cachePrefix + key, value, time);
    }

    /**
     * Set object and expired minute
     */
    @ApiOperation(value = "/setObjectExpireMinuteAuthorization", tags = "Save normal cache and expire minute,as key-value")
    @ApiImplicitParams({@ApiImplicitParam(name = "cacheData", value = "object{key,value,hashKey,timeout}", required = true, dataType = "CacheData")})
    @PostMapping("/setObjectExpireMinuteAuthorization")
    public String setObjectExpireMinuteAuthorization(@RequestBody CacheData cacheData) {
        return redisUtil.setObjectExpireMinute(cachePrefix + cacheData.getKey(), cacheData.getValue(), cacheData.getTimeout());
    }

    /**
     * Set object and expired hours
     */
    @ApiOperation(value = "/setObjectCacheExpireHour", tags = "Save normal cache and expire hour,as key-value")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "key", value = "key", required = true, dataType = "String"),
            @ApiImplicitParam(name = "value", value = "value", required = true, dataType = "String"),
            @ApiImplicitParam(name = "time", value = "time", required = true, dataType = "Long")
    })
    @PostMapping("/setObjectCacheExpireHour")
    public String setObjectExpireHour(@RequestParam("key") String key, @RequestParam("value") String value, @RequestParam("time") Long time) {
        return redisUtil.setObjectExpireHour(cachePrefix + key, value, time);
    }

    /**
     * Set object and expired days
     */
    @ApiOperation(value = "/setObjectCacheExpireDay", tags = "Save normal cache and expire day,as key-value")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "key", value = "key", required = true, dataType = "String"),
            @ApiImplicitParam(name = "value", value = "value", required = true, dataType = "String"),
            @ApiImplicitParam(name = "time", value = "time", required = true, dataType = "Long")
    })
    @PostMapping("/setObjectCacheExpireDay")
    public String setObjectExpireDay(@RequestParam("key") String key, @RequestParam("value") String value, @RequestParam("time") Long time) {
        return redisUtil.setObjectExpireDay(cachePrefix + key, value, time);
    }

    /**
     * Set hash
     */
    @ApiOperation(value = "/setHashCache", tags = "Save hash cache,as key-value{hashKey-value}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "key", value = "key", required = true, dataType = "String"),
            @ApiImplicitParam(name = "hashKey", value = "hashKey", required = true, dataType = "String"),
            @ApiImplicitParam(name = "value", value = "value", required = true, dataType = "String")
    })
    @PostMapping("/setHashCache")
    public String setHash(@RequestParam("key") String key, @RequestParam("hashKey") String hashKey, @RequestParam("value") String value) {
        return redisUtil.setHash(cachePrefix + key, hashKey, value);
    }

    /**
     * Get hash
     */
    @ApiOperation(value = "/getHashCache", tags = "Get hash cahe")
    @ApiImplicitParams({@ApiImplicitParam(name = "key", value = "redis cache key", required = true, dataType = "String"),
            @ApiImplicitParam(name = "hashKey", value = "redis hashCache hashKey", required = true, dataType = "String")})
    @GetMapping("/getHashCache")
    public Object getHash(@RequestParam("key") String key, @RequestParam("hashKey") String hashKey) {
        Object obj = redisUtil.getHash(cachePrefix + key, hashKey);
        return obj == null ? "" : obj;
    }

    /**
     * Delete hash
     */
    @ApiOperation(value = "/deleteHashCache", tags = "Delete hash cache")
    @ApiImplicitParams({@ApiImplicitParam(name = "key", value = "redis cache key", required = true, dataType = "String"),
            @ApiImplicitParam(name = "hashKey", value = "redis hashCache hashKey", required = true, dataType = "String")})
    @GetMapping("/deleteHashCache")
    public String deleteHash(@RequestParam("key") String key, @RequestParam("hashKey") String hashKey) {
        return redisUtil.deleteHash(cachePrefix + key, hashKey);
    }

    /**
     * Delete hash of matching key
     */
    @ApiOperation(value = "/deleteLikeHashCache", tags = "Delete like hash cache")
    @ApiImplicitParams({@ApiImplicitParam(name = "key", value = "redis cache key", required = true, dataType = "String"),
            @ApiImplicitParam(name = "hashKey", value = "redis hashCache hashKey", required = true, dataType = "String")})
    @GetMapping("/deleteLikeHashCache")
    public String deleteLikeHash(@RequestParam("key") String key, @RequestParam("hashKey") String hashKey) {
        return redisUtil.deleteLikeHash(cachePrefix + key, hashKey);
    }

    /**
     * Delete hash of left matching key
     */
    @ApiOperation(value = "/deleteLeftLikeHashCache", tags = "Delete left like hash cache")
    @ApiImplicitParams({@ApiImplicitParam(name = "key", value = "redis cache key", required = true, dataType = "String"),
            @ApiImplicitParam(name = "hashKey", value = "redis hashCache hashKey", required = true, dataType = "String")})
    @GetMapping("/deleteLeftLikeHashCache")
    public String deleteLeftLikeHash(@RequestParam("key") String key, @RequestParam("hashKey") String hashKey) {
        return redisUtil.deleteLeftLikeHash(cachePrefix + key, hashKey);
    }

    /**
     * Set hash and expired minute
     */
    @ApiOperation(value = "/setHashCacheExpireMinute", tags = "Save hash cache and expired minute, as key-value{hashKey-value}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "key", value = "key", required = true, dataType = "String"),
            @ApiImplicitParam(name = "hashKey", value = "hashKey", required = true, dataType = "String"),
            @ApiImplicitParam(name = "value", value = "value", required = true, dataType = "String"),
            @ApiImplicitParam(name = "time", value = "time", required = true, dataType = "Long")
    })
    @PostMapping("/setHashCacheExpireMinute")
    public String setHashExpireMinute(@RequestParam("key") String key, @RequestParam("hashKey") String hashKey, @RequestParam("value") String value, @RequestParam("time") Long time) {
        return redisUtil.setHashExpireMinute(cachePrefix + key, hashKey, value, time);
    }

    /**
     * Set hash and expired hours
     */
    @ApiOperation(value = "/setHashCacheExpireHour", tags = "Save hash cache and expired hours, as key-value{hashKey-value}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "key", value = "key", required = true, dataType = "String"),
            @ApiImplicitParam(name = "hashKey", value = "hashKey", required = true, dataType = "String"),
            @ApiImplicitParam(name = "value", value = "value", required = true, dataType = "String"),
            @ApiImplicitParam(name = "time", value = "time", required = true, dataType = "Long")
    })
    @PostMapping("/setHashCacheExpireHour")
    public String setHashExpireHour(@RequestParam("key") String key, @RequestParam("hashKey") String hashKey, @RequestParam("value") String value, @RequestParam("time") Long time) {
        return redisUtil.setHashExpireHour(cachePrefix + key, hashKey, value, time);
    }

    /**
     * Set hash and expired days
     */
    @ApiOperation(value = "/setHashCacheExpireDay", tags = "Save hash cache and expired days, as key-value{hashKey-value}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "key", value = "key", required = true, dataType = "String"),
            @ApiImplicitParam(name = "hashKey", value = "hashKey", required = true, dataType = "String"),
            @ApiImplicitParam(name = "value", value = "value", required = true, dataType = "String"),
            @ApiImplicitParam(name = "time", value = "time", required = true, dataType = "Long")
    })
    @PostMapping("/setHashCacheExpireDay")
    public String setHashExpireDay(@RequestParam("key") String key, @RequestParam("hashKey") String hashKey, @RequestParam("value") String value, @RequestParam("time") Long time) {
        return redisUtil.setHashExpireDay(cachePrefix + key, hashKey, value, time);
    }

    /**
     * Set list
     */
    @ApiOperation(value = "/setListCache", tags = "Save list cache, as key-value[]")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "key", value = "key", required = true, dataType = "String"),
            @ApiImplicitParam(name = "value", value = "value", required = true, dataType = "String")
    })
    @PostMapping("/setListCache")
    public String setList(String key, String value) {
        return redisUtil.setList(cachePrefix + key, value);
    }

    /**
     * Get list
     */
    @ApiOperation(value = "/getListCache", tags = "Get list cache")
    @ApiImplicitParams({@ApiImplicitParam(name = "key", value = "redis cache key", required = true, dataType = "CacheData")})
    @GetMapping("/getListCache")
    public Object getList(@RequestParam("key") String key) {
        try {
            return redisUtil.getList(cachePrefix + key);
        } catch (Exception e) {
            logger.error("Error while getting list:" + e.getMessage());
            return "false";
        }
    }

    /**
     * Delete List
     */
    @ApiOperation(value = "/deleteListCache", tags = "Delete list cache")
    @ApiImplicitParams({@ApiImplicitParam(name = "key", value = "redis cache key", required = true, dataType = "String")})
    @GetMapping("/deleteListCache")
    public String deleteList(@RequestParam("key") String key) {
        return redisUtil.deleteList(cachePrefix + key);
    }

    /**
     * Set list and expired minute
     */
    @ApiOperation(value = "/setListCacheExpireMinute", tags = "Save list cache and expired minute, as key-value[]")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "key", value = "key", required = true, dataType = "String"),
            @ApiImplicitParam(name = "value", value = "value", required = true, dataType = "String"),
            @ApiImplicitParam(name = "time", value = "time", required = true, dataType = "Long")
    })
    @PostMapping("/setListCacheExpireMinute")
    public String setListExpireMinute(@RequestParam("key") String key, @RequestParam("value") String value, @RequestParam("time") Long time) {
        return redisUtil.setListExpireMinute(cachePrefix + key, value, time);
    }

    /**
     * Set list and expired hours
     */
    @ApiOperation(value = "/setListCacheExpireHour", tags = "Save list cache and expired hours, as key-value[]")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "key", value = "key", required = true, dataType = "String"),
            @ApiImplicitParam(name = "value", value = "value", required = true, dataType = "String"),
            @ApiImplicitParam(name = "time", value = "time", required = true, dataType = "Long")
    })
    @PostMapping("/setListCacheExpireHour")
    public String setListExpireHour(@RequestParam("key") String key, @RequestParam("value") String value, @RequestParam("time") Long time) {
        return redisUtil.setListExpireHour(cachePrefix + key, value, time);
    }

    /**
     * Set list and expired days
     */
    @ApiOperation(value = "/setListCacheExpireDay", tags = "Save list cache and expired days, as key-value[]")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "key", value = "key", required = true, dataType = "String"),
            @ApiImplicitParam(name = "value", value = "value", required = true, dataType = "String"),
            @ApiImplicitParam(name = "time", value = "time", required = true, dataType = "Long")
    })
    @PostMapping("/setListCacheExpireDay")
    public String setListExpireDay(@RequestParam("key") String key, @RequestParam("value") String value, @RequestParam("time") Long time) {
        return redisUtil.setListExpireDay(cachePrefix + key, value, time);
    }

    /**
     * Delete all cache
     */
    @ApiOperation(value = "/deleteAllCache", tags = "Delete all cache")
    @GetMapping("/deleteAllCache")
    public String deleteAll() {
        return redisUtil.deleteAll(cachePrefix);
    }

    /**
     * Get cache size
     */
    @ApiOperation(value = "/sizeCache", tags = "Get cache size")
    @GetMapping("/sizeCache")
    public int cacheSize() {
        return redisUtil.size();
    }

    /**
     * Get all cache keys
     */
    @ApiOperation(value = "/keysCache", tags = "Get all cache keys")
    @GetMapping("/keysCache")
    public Set<String> cacheKeys() {
        Set<String> keys = redisUtil.keys();
        return keys;
    }

    /**
     * Get all cache values
     */
    @ApiOperation(value = "/valuesCache", tags = "Get all cache values")
    @GetMapping("/valuesCache")
    public Collection<Object> cacheValues() {
        Collection<Object> list = redisUtil.values();
        return list;
    }

    /**
     * Get cache size of matching key
     * @param key
     * @return
     */
    @ApiOperation(value = "/sizeLike", tags = "Get cache size of matching key")
    @GetMapping("/sizeLike")
    public Integer sizeLike(@RequestParam("key") String key) {
        return redisUtil.sizeLike(cachePrefix + key);
    }

    /**
     * Get key set of matching the key
     */
    @ApiOperation(value = "/keysLike", tags = "Get key set of matching the key")
    @GetMapping("/keysLike")
    public Set<String> keysLike(@RequestParam("key") String key) {
        Set<String> keys = redisUtil.keysLike(cachePrefix + key);
        return keys;
    }

    /**
     * Get value collection of matching the key
     */
    @ApiOperation(value = "/valuesLike", tags = "Get value collection of matching the keys")
    @GetMapping("/valuesLike")
    public Collection<Object> valuesLike(@RequestParam("key") String key) {
        Collection<Object> list = redisUtil.valuesLike(cachePrefix + key);
        return list;
    }

    /**
     * Determine whether the key exists
     */
    @ApiOperation(value = "/existKey", tags = "Whether a cache exists")
    @GetMapping("/existKey")
    public Boolean exist(@RequestParam("key") String key) {
        return redisUtil.exist(cachePrefix + key);
    }

    /**
     * Get key expiration time
     */
    @ApiOperation(value = "/expireKey", tags = "Get key expiration time")
    @GetMapping("/expireKey")
    public Long expire(@RequestParam("key") String key) {
        return redisUtil.expire(cachePrefix + key);
    }

    /**
     * Delete cache of matching key
     */
    @ApiOperation(value = "/deleteLike", tags = "Delete cache of matching key")
    @GetMapping("/deleteLike")
    public String deleteLike(@RequestParam("key") String key) {
        return redisUtil.deleteLike(cachePrefix + key);
    }

    /**
     * Save set
     */
    @ApiOperation(value = "/setSet", tags = "Save set cache")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "key", value = "key", required = true, dataType = "String"),
            @ApiImplicitParam(name = "value", value = "value", required = true, dataType = "String")
    })
    @PostMapping("/setSet")
    public String setSet(@RequestParam("key") String key, @RequestParam("value") String value) {
        return redisUtil.setSet(cachePrefix + key, value);
    }

    /**
     * Save set and expired minute
     */
    @ApiOperation(value = "/setSetExpireMinute", tags = "Save set cache and expired minute")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "key", value = "key", required = true, dataType = "String"),
            @ApiImplicitParam(name = "value", value = "value", required = true, dataType = "String"),
            @ApiImplicitParam(name = "time", value = "time", required = true, dataType = "Long")
    })
    @PostMapping("/setSetExpireMinute")
    public String setSetExpireMinute(@RequestParam("key") String key, @RequestParam("value") String value, @RequestParam("time") Long time) {
        return redisUtil.setSetExpireMinute(cachePrefix + key, value, time);
    }

    /**
     * Save set and expired hours
     */
    @ApiOperation(value = "/setSetExpireHour", tags = "Save set cache and expired hours")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "key", value = "key", required = true, dataType = "String"),
            @ApiImplicitParam(name = "value", value = "value", required = true, dataType = "String"),
            @ApiImplicitParam(name = "time", value = "time", required = true, dataType = "Long")
    })
    @PostMapping("/setSetExpireHour")
    public String setSetExpireHour(@RequestParam("key") String key, @RequestParam("value") String value, @RequestParam("time") Long time) {
        return redisUtil.setSetExpireHour(cachePrefix + key, value, time);
    }

    /**
     * Save set and expired days
     */
    @ApiOperation(value = "/setSetExpireDay", tags = "Save set cache and expired days")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "key", value = "key", required = true, dataType = "String"),
            @ApiImplicitParam(name = "value", value = "value", required = true, dataType = "String"),
            @ApiImplicitParam(name = "time", value = "time", required = true, dataType = "Long")
    })
    @PostMapping("/setSetExpireDay")
    public String setSetExpireDay(@RequestParam("key") String key, @RequestParam("value") String value, @RequestParam("time") Long time) {
        return redisUtil.setSetExpireDay(cachePrefix + key, value, time);
    }

    /**
     * Save zset
     */
    @ApiOperation(value = "/setZset", tags = "Save Zset cache")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "key", value = "key", required = true, dataType = "String"),
            @ApiImplicitParam(name = "value", value = "value", required = true, dataType = "String"),
            @ApiImplicitParam(name = "num", value = "num", required = true, dataType = "String")
    })
    @PostMapping("/setZset")
    public String setZset(@RequestParam("key") String key, @RequestParam("value") String value, @RequestParam("num") String num) {
        return redisUtil.setZset(cachePrefix + key, value, Double.valueOf(num));
    }

    /**
     * Save zset and expired minute
     */
    @ApiOperation(value = "/setZsetExpireMinute", tags = "Save zset cache and expired minute")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "key", value = "key", required = true, dataType = "String"),
            @ApiImplicitParam(name = "value", value = "value", required = true, dataType = "String"),
            @ApiImplicitParam(name = "num", value = "num", required = true, dataType = "String"),
            @ApiImplicitParam(name = "time", value = "time", required = true, dataType = "Long")
    })
    @PostMapping("/setZsetExpireMinute")
    public String setZsetExpireMinute(@RequestParam("key") String key, @RequestParam("value") String value, @RequestParam("num") String num, @RequestParam("time") Long time) {
        return redisUtil.setZsetExpireMinute(cachePrefix + key, value, Double.valueOf(num), time);
    }

    /**
     * Save zset and expired hours
     */
    @ApiOperation(value = "/setZsetExpireHour", tags = "Save zset cache and expired hours")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "key", value = "key", required = true, dataType = "String"),
            @ApiImplicitParam(name = "value", value = "value", required = true, dataType = "String"),
            @ApiImplicitParam(name = "num", value = "num", required = true, dataType = "String"),
            @ApiImplicitParam(name = "time", value = "time", required = true, dataType = "Long")
    })
    @PostMapping("/setZsetExpireHour")
    public String setZsetExpireHour(@RequestParam("key") String key, @RequestParam("value") String value, @RequestParam("num") String num, @RequestParam("time") Long time) {
        return redisUtil.setZsetExpireHour(cachePrefix + key, value, Double.valueOf(num), time);
    }

    /**
     * Save zset and expired days
     */
    @ApiOperation(value = "/setZsetExipreDay", tags = "Save zset cache and expired days")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "key", value = "key", required = true, dataType = "String"),
            @ApiImplicitParam(name = "value", value = "value", required = true, dataType = "String"),
            @ApiImplicitParam(name = "num", value = "num", required = true, dataType = "String"),
            @ApiImplicitParam(name = "time", value = "time", required = true, dataType = "Long")
    })
    @PostMapping("/setZsetExipreDay")
    public String setZsetExipreDay(@RequestParam("key") String key, @RequestParam("value") String value, @RequestParam("num") String num, @RequestParam("time") Long time) {
        return redisUtil.setZsetExipreDay(cachePrefix + key, value, Double.valueOf(num), time);
    }

    /**
     * Get hash keys
     */
    @ApiOperation(value = "/getHashKeys", tags = "Get hash keys")
    @ApiImplicitParams(@ApiImplicitParam(name = "key", value = "key", required = true, dataType = "String"))
    @GetMapping("/getHashKeys")
    public Set<String> getHashKeys(@RequestParam("key") String key) {
        Set<String> keys = redisUtil.getHashKeys(cachePrefix + key);
        return keys;
    }
}
