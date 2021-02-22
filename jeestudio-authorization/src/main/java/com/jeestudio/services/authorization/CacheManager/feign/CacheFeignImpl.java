package com.jeestudio.services.authorization.CacheManager.feign;

import com.jeestudio.utils.CacheData;
import com.jeestudio.utils.PropertiesUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Collection;
import java.util.Set;

/**
 * @Description: Cache impl
 * @author: whl
 * @Date: 2019-12-05
 */
@Component
public class CacheFeignImpl {

    private RestTemplate restTemplate;

    private String URL = "";

    public CacheFeignImpl() {
        this.restTemplate = new RestTemplate();
        URL = PropertiesUtil.getProperty("serverIp.properties", "cacheServerName");
    }

    public String getString(String key) {
        return restTemplate.getForObject(URL + "getObjectAuthorization?key=" + key, String.class);
    }

    public Object getObject(String key) {
        return restTemplate.getForObject(URL + "getObjectCache?key=" + key, Object.class);
    }

    public String deleteObject(String key) {
        return restTemplate.getForObject(URL + "deleteObjectCache?key=" + key, String.class);
    }

    public String setObjectExpireMinute(String key, Object object, Long timeout) {
        CacheData cacheData = new CacheData();
        cacheData.setKey(key);
        cacheData.setValue(object);
        cacheData.setTimeout(timeout);
        return restTemplate.postForObject(URL + "setObjectExpireMinuteAuthorization", cacheData, String.class);
    }

    public String deleteAll() {
        return restTemplate.getForObject(URL + "deleteAllCache", String.class);
    }

    public Integer sizeLike(String key) {
        return restTemplate.getForObject(URL + "sizeLike?key=" + key, Integer.class);
    }

    public Set<String> keysLike(String key) {
        return restTemplate.getForObject(URL + "keysLike?key=" + key, Set.class);
    }

    public Collection<Object> valuesLike(String key) {
        return restTemplate.getForObject(URL + "valuesLike?key=" + key, Collection.class);
    }

    public Boolean exist(String key) {
        return restTemplate.getForObject(URL + "existKey?key=" + key, Boolean.class);
    }

    public Long expire(String key) {
        return restTemplate.getForObject(URL + "expireKey?key=" + key, Long.class);
    }
}
