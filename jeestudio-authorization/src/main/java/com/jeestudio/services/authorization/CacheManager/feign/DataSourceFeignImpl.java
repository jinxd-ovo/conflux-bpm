package com.jeestudio.services.authorization.CacheManager.feign;

import com.jeestudio.common.entity.system.User;
import com.jeestudio.utils.PropertiesUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * @Description: Datasource feign impl
 * @author: whl
 * @Date: 2019-12-12
 */
@Component
public class DataSourceFeignImpl {
    private RestTemplate restTemplate;

    private String URL = "";

    public DataSourceFeignImpl() {
        this.restTemplate = new RestTemplate();
        URL = PropertiesUtil.getProperty("serverIp.properties", "datasourceServerName");
    }

    public LinkedHashMap<String, List<String>> getUserPermissionByLoginName(String loginName) {
        return restTemplate.getForObject(URL + "user/getUserPermissionByLoginName?loginName=" + loginName, LinkedHashMap.class);
    }

    public User getUserMsgByLoginName(String loginName) {
        return restTemplate.getForObject(URL + "user/getUserMsgByLoginName?loginName=" + loginName, User.class);
    }
}
