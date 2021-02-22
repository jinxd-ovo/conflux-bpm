package com.jeestudio.cache;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;

/**
 * @Description: Startup class
 * @author: whl
 * @Date: 2019-11-26
 */
@EnableEurekaClient
@EnableHystrixDashboard
@EnableHystrix
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class ApplicationCache {
    public static void main(String[] args) {
        SpringApplication.run(ApplicationCache.class,args);
    }
}
