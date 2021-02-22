package com.jeestudio.datasource.config;

import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.mapping.VendorDatabaseIdProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * @Description: Datasource config
 * @author: David
 * @Date: 2020-02-03
 */
@Configuration
public class DataSourceConfig {

    @Bean
    public DatabaseIdProvider getDatabaseIdProvider() {
        DatabaseIdProvider databaseIdProvider = new VendorDatabaseIdProvider();
        Properties properties = new Properties();
        properties.setProperty("Oracle","oracle");
        properties.setProperty("MySQL","mysql");
        properties.setProperty("DB2","db2");
        properties.setProperty("Derby","derby");
        properties.setProperty("H2","h2");
        properties.setProperty("HSQL","hsql");
        properties.setProperty("Informix","informix");
        properties.setProperty("MS-SQL","mssql");
        properties.setProperty("SQL Server", "mssql");
        properties.setProperty("PostgreSQL","postgre");
        properties.setProperty("Sybase","sybase");
        properties.setProperty("Hana","hana");
        properties.setProperty("DM","dm");
        databaseIdProvider.setProperties(properties);
        return databaseIdProvider;
    }
}
