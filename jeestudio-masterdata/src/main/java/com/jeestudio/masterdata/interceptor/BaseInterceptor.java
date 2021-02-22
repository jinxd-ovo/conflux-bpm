package com.jeestudio.masterdata.interceptor;

import com.jeestudio.common.entity.common.persistence.Page;
import com.jeestudio.utils.Reflections;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.plugin.Interceptor;

import java.io.Serializable;

/**
 * @Description: Mybatis page blocker base class
 * @author: whl
 * @Date: 2019-11-28
 */
public abstract class BaseInterceptor implements Interceptor, Serializable {

    private static final long serialVersionUID = 1L;
    protected static final String PAGE = "page";
    protected static final String DELEGATE = "delegate";
    protected static final String MAPPED_STATEMENT = "mappedStatement";
    protected Log log = LogFactory.getLog(this.getClass());

    @SuppressWarnings("unchecked")
    protected static Page<Object> convertParameter(Object parameterObject, Page<Object> page) {
        try {
            if (parameterObject instanceof Page) {
                return (Page<Object>) parameterObject;
            } else {
                return (Page<Object>) Reflections.getFieldValue(parameterObject, PAGE);
            }
        } catch (Exception e) {
            return null;
        }
    }
}
