package com.jeestudio.utils;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

/**
 * @Description: Save the spring ApplicationContext as a static variable,
 * and take out the applicatoncontext at any time anywhere in the code.
 * @author: whl
 * @Date: 2019-11-20
 */
@Service
@Lazy(false)
public class SpringContextHolder implements ApplicationContextAware, DisposableBean {

    private static Logger logger = LoggerFactory.getLogger(SpringContextHolder.class);

    private static ApplicationContext applicationContext = null;

    /**
     * Get the ApplicationContext stored in the static variable
     */
    public static ApplicationContext getApplicationContext() {
        assertContextInjected();
        return applicationContext;
    }

    /**
     * Get the bean from the static variable ApplicationContext, and automatically transform it to the type of the assigned object.
     */
    public static <T> T getBean(String name) {
        assertContextInjected();
        return (T) applicationContext.getBean(name);
    }

    /**
     * Get the bean from the static variable ApplicationContext, and automatically transform it to the type of the assigned object.
     */
    public static <T> T getBean(Class<T> requiredType) {
        assertContextInjected();
        return applicationContext.getBean(requiredType);
    }

    /**
     * Clearing ApplicationContext in springcontextholder is null
     */
    public static void clearHolder() {
        if (logger.isDebugEnabled()){
            logger.debug("Clearn SpringContextHolder of ApplicationContext, " + applicationContext);
        }
        applicationContext = null;
    }

    /**
     * Implement the applicationcontextaware interface and inject context into static variables
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        SpringContextHolder.applicationContext = applicationContext;
    }

    /**
     * Implement the disposablebean interface to clean up static variables when context is closed
     */
    @Override
    public void destroy() throws Exception {
        SpringContextHolder.clearHolder();
    }

    /**
     * Check that ApplicationContext is not empty
     */
    private static void assertContextInjected() {
        Validate.validState(applicationContext != null, "The ApplicationContext property is not injected, please define springcontextholder in applicationcontext.xml.");
    }
}
