package com.jeestudio.datasource.service.ai;

import com.jeestudio.utils.HttpUtil;
import com.jeestudio.utils.MD5;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description: Trans Service
 * @author: David
 * @Date: 2020-03-05
 */
@Service
public class TransService {

    protected static final Logger logger = LoggerFactory.getLogger(TransService.class);

    private static final String TRANS_API_HOST = "http://api.fanyi.baidu.com/api/trans/vip/translate";

    @Value("${baidu.appid}")
    private String appid;

    @Value("${baidu.securityKey}")
    private String securityKey;

    public String getTransResult(String query, String from, String to) {
        String result = "";
        try {
            Map<String, String> params = buildParams(query, from, to);
            result = HttpUtil.get(TRANS_API_HOST, params);
        } catch (Exception e) {
            logger.error("Error while translating:" + ExceptionUtils.getStackTrace(e));
        }
        return result;
    }

    public String getTransResultString(String query, String from, String to) {
        String resultString = "";
        try {
            Map<String, String> params = buildParams(query, from, to);
            String result = HttpUtil.get(TRANS_API_HOST, params);
            if (result.indexOf("\"dst\":\"") != -1) {
                result = result.substring(result.indexOf("\"dst\":\"") + 7);
                if (result.indexOf("\"") != -1) {
                    result = result.substring(0, result.indexOf("\""));
                    resultString = result;
                }
            }
        } catch (Exception e) {
            logger.error("Error while translating:" + ExceptionUtils.getStackTrace(e));
        }
        return resultString;
    }

    private Map<String, String> buildParams(String query, String from, String to) throws UnsupportedEncodingException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("q", query);
        params.put("from", from);
        params.put("to", to);

        params.put("appid", appid);

        String salt = String.valueOf(System.currentTimeMillis());
        params.put("salt", salt);

        String src = appid + query + salt + securityKey;
        params.put("sign", MD5.md5(src));

        return params;
    }
}