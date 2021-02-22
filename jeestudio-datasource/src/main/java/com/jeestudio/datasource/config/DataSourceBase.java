package com.jeestudio.datasource.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

/**
 * @Description: First database profile
 * @author: whl
 * @Date: 2019-11-28
 */
@Configuration
@MapperScan(basePackages = "com.jeestudio.datasource.mapper.base.*", sqlSessionFactoryRef = "sqlSessionFactoryBase")
public class DataSourceBase {

    private static final String MAPPER_XML = "classpath:mapper/mappers/base/*/*.xml";
    private static final String ENTITY = "com.jeestudio.common.entity.*,com.jeestudio.common.view.*";
    private static final String CONFIG = "classpath:mapper/mybatis-config.xml";

    /**
     * Read the corresponding value of spring.datasource.dbOne from the global configuration file to build the data source object
     * @return
     */
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.dbbase")
    @Primary
    public DataSource dataSourceDbBase(){
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "sqlSessionFactoryBase")
    @Primary
    public SqlSessionFactory sqlSessionFactoryBase() throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSourceDbBase());
        sqlSessionFactoryBean.setMapperLocations(new PathMatchingResourcePatternResolver()
                .getResources(DataSourceBase.MAPPER_XML));
        sqlSessionFactoryBean.setTypeAliasesPackage(DataSourceBase.ENTITY);
        sqlSessionFactoryBean.setConfigLocation(new PathMatchingResourcePatternResolver()
                .getResource(DataSourceBase.CONFIG));
        return sqlSessionFactoryBean.getObject();
    }

    @Bean(name = "transactionManagerBase")
    @Primary
    public DataSourceTransactionManager transactionManagerBase(){
        return new DataSourceTransactionManager(dataSourceDbBase());
    }
}
