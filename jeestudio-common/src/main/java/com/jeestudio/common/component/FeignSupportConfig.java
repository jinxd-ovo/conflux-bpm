package com.jeestudio.common.component;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Description: Feign token config
 * @author: whl
 * @Date: 2020-01-16
 */
@Configuration
public class FeignSupportConfig {

    @Bean
    public RequestInterceptor requestInterceptor(){
        return new FeignTokenInterceptor();
    }

}
