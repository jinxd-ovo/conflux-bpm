package com.jeestudio.datasource.service.async;

import com.jeestudio.datasource.feign.CacheFeign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * @Description: Async Service
 * @author: whl
 * @Date: 2019-12-27
 */
@Component
public class AsyncService {

    @Autowired
    private CacheFeign cacheFeign;

    @Async
    public void asyncSaveHashCache(String key, String hashKey, String value) {
        cacheFeign.setHash(key, hashKey, value);
    }

    @Async
    public void deleteListHash(String key, String hashKey) {
        cacheFeign.deleteLikeHash(key, hashKey);
    }

    @Async
    public void deleteListHashStartWithHashKey(String key, String hashKey) {
        cacheFeign.deleteLeftLikeHash(key, hashKey);
    }
}
