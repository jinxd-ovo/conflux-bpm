package com.jeestudio.common.component;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import feign.Retryer;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * @Description: Feign token interceptor
 * @author: whl
 * @Date: 2019-12-05
 */
public class FeignTokenInterceptor implements RequestInterceptor {

    public Retryer feignRetryer(){
        return new Retryer.Default(100,SECONDS.toMillis(1),5);
    }

    @Override
    public void apply(RequestTemplate requestTemplate) {
        if (null == getHttpServletRequest()){
            return;
        }
        requestTemplate.header("token",getHttpServletRequest().getHeader("token"));
    }

    private HttpServletRequest getHttpServletRequest(){
        try{
            return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        }catch (Exception e){
            return null;
        }
    }
}
