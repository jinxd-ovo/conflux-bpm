package com.jeestudio.datasource.config;

import com.jeestudio.utils.IdGen;
import org.activiti.engine.*;
import org.activiti.rest.common.application.DefaultContentTypeResolver;
import org.activiti.rest.service.api.RestResponseFactory;
import org.activiti.spring.ProcessEngineFactoryBean;
import org.activiti.spring.SpringProcessEngineConfiguration;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

/**
 * @Description: Activiti config
 * @author: whl
 * @Date: 2020-01-13
 */
@Configuration
public class ActivitiConfig {

    private static final Logger logger = LoggerFactory.getLogger(ActivitiConfig.class);

    @Bean
    public SpringProcessEngineConfiguration processEngineConfiguration(DataSource dataSourceDbBase, DataSourceTransactionManager transactionManagerBase) {
        SpringProcessEngineConfiguration processEngineConfiguration = new SpringProcessEngineConfiguration();
        processEngineConfiguration.setDataSource(dataSourceDbBase);
        processEngineConfiguration.setTransactionManager(transactionManagerBase);
        processEngineConfiguration.setDatabaseSchemaUpdate("true");
        processEngineConfiguration.setJobExecutorActivate(true);
        processEngineConfiguration.setHistory("full");
        processEngineConfiguration.setProcessDefinitionCacheLimit(10);
        processEngineConfiguration.setIdGenerator(new IdGen());
        processEngineConfiguration.setActivityFontName("宋体");
        processEngineConfiguration.setLabelFontName("宋体");
        return processEngineConfiguration;
    }

    @Bean
    public ProcessEngineFactoryBean processEngine(SpringProcessEngineConfiguration processEngineConfiguration) {
        ProcessEngineFactoryBean processEngineFactoryBean = new ProcessEngineFactoryBean();
        processEngineFactoryBean.setProcessEngineConfiguration(processEngineConfiguration);
        return processEngineFactoryBean;
    }

    @Bean
    public RestResponseFactory restResponseFactory() {
        return new RestResponseFactory();
    }

    @Bean
    public DefaultContentTypeResolver contentTypeResolver() {
        return new DefaultContentTypeResolver();
    }

    @Bean
    public RepositoryService repositoryService(ProcessEngineFactoryBean processEngine) {
        RepositoryService repositoryService = null;
        try {
            repositoryService = processEngine.getObject().getRepositoryService();
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        }
        return repositoryService;
    }

    @Bean
    public RuntimeService runtimeService(ProcessEngineFactoryBean processEngine) {
        RuntimeService runtimeService = null;
        try {
            runtimeService = processEngine.getObject().getRuntimeService();
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        }
        return runtimeService;
    }

    @Bean
    public FormService formService(ProcessEngineFactoryBean processEngine) {
        FormService formService = null;
        try {
            formService = processEngine.getObject().getFormService();
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        }
        return formService;
    }

    @Bean
    public IdentityService identityService(ProcessEngineFactoryBean processEngine) {
        IdentityService identityService = null;
        try {
            identityService = processEngine.getObject().getIdentityService();
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        }
        return identityService;
    }

    @Bean
    public TaskService taskService(ProcessEngineFactoryBean processEngine) {
        TaskService taskService = null;
        try {
            taskService = processEngine.getObject().getTaskService();
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        }
        return taskService;
    }

    @Bean
    public HistoryService historyService(ProcessEngineFactoryBean processEngine) {
        HistoryService historyService = null;
        try {
            historyService = processEngine.getObject().getHistoryService();
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        }
        return historyService;
    }

    @Bean
    public ManagementService managementService(ProcessEngineFactoryBean processEngine) {
        ManagementService managementService = null;
        try {
            managementService = processEngine.getObject().getManagementService();
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        }
        return managementService;
    }
}
