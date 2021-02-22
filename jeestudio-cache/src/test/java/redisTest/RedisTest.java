package redisTest;

import com.jeestudio.cache.ApplicationCache;
import com.jeestudio.cache.cacheUtils.RedisUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @Description: test
 * @author: whl
 * @Date: 2019-11-26
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ApplicationCache.class)
public class RedisTest {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private HashOperations hashOperations;
    @Autowired
    private ListOperations listOperations;
    @Autowired
    private SetOperations setOperations;
    @Autowired
    private ZSetOperations zSetOperations;
    @Autowired
    private RedisUtil redisUtil;

    @Test
    public void setStringRedisTemplate(){

        /*int n = redisUtil.size();
        Set<String> s = redisUtil.keys();
        Collection<Object> c = redisUtil.values();*/

        /*Set<String> keys = redisTemplate.keys("*");
        redisTemplate.delete(keys);*/
        /*JSONObject obj = new JSONObject();
        JSONObject obj1 = new JSONObject();
        JSONObject obj2 = new JSONObject();
        JSONArray array = new JSONArray();
        try {
            obj.put("name","java");
            obj.put("title","本书标题");
            array.put(obj);
            obj1.put("name","java123");
            obj1.put("title","本书标题123");
            array.put(obj1);
            obj2.put("name","java456");
            obj2.put("title","本书标题456");
            array.put(obj2);
        } catch (JSONException e) {
            e.printStackTrace();
        }*/
        /*redisTemplateObject.opsForValue().set("book",obj);*/
        /*hashOperations.put("hash","book1",obj);
        redisTemplate.expire("hash",1,TimeUnit.MINUTES);
        Object h = hashOperations.get("hash","book1");*/
        /*listOperations.leftPush("list123",array);
        Object l = listOperations.range("list123",0,listOperations.size("list123"));*/
        //Object l = listOperations.leftPop("list123");
        /*setOperations.add("set1",obj);
        Object s = setOperations.randomMember("set1");*/
        //Object s = setOperations.pop("set1");
        /*zSetOperations.add("zset1",obj,1);
        Object z = zSetOperations.range("zset1",0,zSetOperations.size("zset1"));*/
        /*redisTemplateObject.delete("book");
        hashOperations.delete("hash","book1");*/
        //zSetOperations.remove("zset1",obj);:
    }

}
