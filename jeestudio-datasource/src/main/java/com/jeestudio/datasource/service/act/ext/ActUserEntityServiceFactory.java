package com.jeestudio.datasource.service.act.ext;

import org.activiti.engine.impl.interceptor.Session;
import org.activiti.engine.impl.interceptor.SessionFactory;
import org.activiti.engine.impl.persistence.entity.UserIdentityManager;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Description: ActUserEntity Service Factory
 * @author: David
 * @Date: 2020-01-13
 */
public class ActUserEntityServiceFactory implements SessionFactory {

    @Autowired
    private ActUserEntityService actUserEntityService;

    public Class<?> getSessionType() {
        return UserIdentityManager.class;
    }

    public Session openSession() {
        return actUserEntityService;
    }
}
