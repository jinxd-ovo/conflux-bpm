package com.jeestudio.services.authorization.CacheManager;

import com.jeestudio.services.authorization.CacheManager.feign.CacheFeignImpl;
import com.jeestudio.utils.Global;
import com.jeestudio.utils.JwtUtil;
import org.apache.shiro.authz.Permission;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;

import java.util.*;

/**
 * @Description: Cache
 * @author: whl
 * @Date: 2019-11-03
 */
public class RedisCache<K,V> implements Cache<K,V> {

    private CacheFeignImpl cacheFeign;

    public RedisCache() {
        this.cacheFeign = new CacheFeignImpl();
    }

    private String getPrefix(K key) {
        return Global.PREFIX_SHIRO_CACHE + JwtUtil.getCurrentUser(key.toString());
    }

    @Override
    public V get(K k) throws CacheException {
        Object obj = cacheFeign.getObject(this.getPrefix(k));
        LinkedHashMap linkedHashMap = (LinkedHashMap) obj;
        SimpleAuthorizationInfo simpleAuthorizationInfo = null;
        if (linkedHashMap != null) {
            simpleAuthorizationInfo = new SimpleAuthorizationInfo();
            List<String> roles = (List<String>) linkedHashMap.get("roles");
            List<String> stringPermissions = (List<String>) linkedHashMap.get("stringPermissions");
            List<Permission> objectPermissions = (List<Permission>) linkedHashMap.get("objectPermissions");
            if (roles != null) {
                simpleAuthorizationInfo.addRoles(roles);
            }
            if (stringPermissions != null) {
                simpleAuthorizationInfo.addStringPermissions(stringPermissions);
            }
            if (objectPermissions != null) {
                simpleAuthorizationInfo.addObjectPermissions(objectPermissions);
            }
        }
        return (V) simpleAuthorizationInfo;
    }

    @Override
    public V put(K k, V v) throws CacheException {
        return (V) cacheFeign.setObjectExpireMinute(this.getPrefix(k), v, Global.EXRP_MINUTE);
    }

    @Override
    public V remove(K k) throws CacheException {
        return (V) cacheFeign.deleteObject(this.getPrefix(k));
    }

    @Override
    public void clear() throws CacheException {
        cacheFeign.deleteAll();
    }

    @Override
    public int size() {
        Integer num = cacheFeign.sizeLike(Global.PREFIX_SHIRO_CACHE + "*");
        return num == null ? 1 : num;
    }

    @Override
    public Set<K> keys() {
        Set<String> keys = cacheFeign.keysLike(Global.PREFIX_SHIRO_CACHE + "*");
        return (Set<K>) keys;
    }

    @Override
    public Collection<V> values() {
        Collection<Object> values = cacheFeign.valuesLike(Global.PREFIX_SHIRO_CACHE + "*");
        Collection<V> collection = (Collection<V>) values;
        return collection;
    }
}
