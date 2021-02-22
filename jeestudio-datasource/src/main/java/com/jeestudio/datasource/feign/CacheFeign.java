package com.jeestudio.datasource.feign;

import com.jeestudio.common.component.FeignSupportConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Set;

/**
 * @Description: Feign
 * @author: whl
 * @Date: 2019-11-30
 */
@FeignClient(value = "cache",configuration = FeignSupportConfig.class)
@Component
public interface CacheFeign {

    /**
     * Save object cache
     *
     * @param key
     * @param value
     * @return string
     */
    @PostMapping("/cache/setObjectCache")
    String setObject(@RequestParam("key") String key, @RequestParam("value") String value);

    /**
     * Get object cache
     *
     * @param key
     * @return object
     */
    @GetMapping("/cache/getObjectCache")
    Object getObject(@RequestParam("key") String key);

    /**
     * Save hash cache
     *
     * @param key
     * @param hashKey
     * @param value
     * @return hash string
     */
    @PostMapping("/cache/setHashCache")
    String setHash(@RequestParam("key") String key, @RequestParam("hashKey") String hashKey, @RequestParam("value") String value);

    /**
     * Get hash cache
     *
     * @param key
     * @param hashKey
     * @return hash string
     */
    @GetMapping("/cache/getHashCache")
    String getHash(@RequestParam("key") String key, @RequestParam("hashKey") String hashKey);

    /**
     * Delete hash cache
     *
     * @param key
     * @param hashKey
     * @return "true" or "false"
     */
    @GetMapping("/cache/deleteHashCache")
    String deleteHash(@RequestParam("key") String key, @RequestParam("hashKey") String hashKey);

    /**
     * Delete hash cache
     *
     * @param key
     * @param hashKey
     * @return "true" or "false"
     */
    @GetMapping("/cache/deleteLikeHashCache")
    String deleteLikeHash(@RequestParam("key") String key, @RequestParam("hashKey") String hashKey);

    /**
     * Delete hash cache
     *
     * @param key
     * @param hashKey
     * @return "true" or "false"
     */
    @GetMapping("/cache/deleteLeftLikeHashCache")
    String deleteLeftLikeHash(@RequestParam("key") String key, @RequestParam("hashKey") String hashKey);

    /**
     * Delete object cache
     *
     * @param key
     * @return "true" or "false"
     */
    @GetMapping("/cache/deleteObjectCache")
    String deleteObject(@RequestParam("key") String key);

    /**
     * Get hash keys
     *
     * @param key
     * @return string set
     */
    @GetMapping("/cache/getHashKeys")
    Set<String> getHashKeys(@RequestParam("key") String key);
}
