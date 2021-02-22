package com.jeestudio.datasource.interceptor;

import com.jeestudio.common.entity.common.persistence.Page;
import com.jeestudio.datasource.contextHolder.ApplicationContextHolder;
import com.jeestudio.datasource.interceptor.dialectImpl.*;
import com.jeestudio.utils.Reflections;
import com.jeestudio.utils.StringUtil;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * @Description: Database Paging plug-in, which only intercepts query statements
 * @author: whl
 * @Date: 2019-11-28
 */
@Intercepts({ @Signature(type = Executor.class, method = "query", args = {
        MappedStatement.class, Object.class, RowBounds.class,
        ResultHandler.class }) })
public class PaginationInterceptor extends BaseInterceptor {

    private static final long serialVersionUID = 1L;

    protected Dialect getDialect() {
        Dialect dialect = null;
        String dbType = "mysql";
        try{
            dbType = ApplicationContextHolder.getBean(DatabaseIdProvider.class).getDatabaseId(ApplicationContextHolder.getBean(DataSource.class));
        }catch (Exception e){
            log.warn("Getting dbType error, set to default mysql.");
            dbType = "mysql";
        }

        if ("db2".equals(dbType)){
            dialect = new DB2Dialect();
        }else if("derby".equals(dbType)){
            dialect = new DerbyDialect();
        }else if("h2".equals(dbType)){
            dialect = new H2Dialect();
        }else if("hsql".equals(dbType)){
            dialect = new HSQLDialect();
        }else if("mysql".equals(dbType)){
            dialect = new MySQLDialect();
        }else if("oracle".equals(dbType)){
            dialect = new OracleDialect();
        }else if("postgre".equals(dbType)){
            dialect = new PostgreSQLDialect();
        }else if("mssql".equals(dbType) || "sqlserver".equals(dbType)){
            dialect = new SQLServer2005Dialect();
        }else if("sybase".equals(dbType)){
            dialect = new SybaseDialect();
        }else if("dm".equals(dbType)){
            dialect = new DMDialect();
        }
        if (dialect == null) {
            throw new RuntimeException("mybatis dialect error.");
        }
        return dialect;
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {

        final MappedStatement mappedStatement = (MappedStatement) invocation
                .getArgs()[0];

        Object parameter = invocation.getArgs()[1];
        BoundSql boundSql = mappedStatement.getBoundSql(parameter);
        Object parameterObject = boundSql.getParameterObject();

        // Get paging parameter object
        Page<Object> page = null;
        if (parameterObject != null) {
            page = convertParameter(parameterObject, page);
        }

        // If the paging object is set, paging occurs
        if (page != null && page.getPageSize() != -1) {

            if (StringUtil.isBlank(boundSql.getSql())) {
                return null;
            }
            String originalSql = boundSql.getSql().trim();

            // Get the total number of records
            page.setCount(SQLHelper.getCount(originalSql, null,
                    mappedStatement, parameterObject, boundSql, log));

            Dialect dialect = getDialect();
            // Paging query localized object modify database pay attention to the implementation of modification
            String pageSql = SQLHelper.generatePageSql(originalSql, page, dialect);

            invocation.getArgs()[2] = new RowBounds(RowBounds.NO_ROW_OFFSET,
                    RowBounds.NO_ROW_LIMIT);
            BoundSql newBoundSql = new BoundSql(
                    mappedStatement.getConfiguration(), pageSql,
                    boundSql.getParameterMappings(),
                    boundSql.getParameterObject());
            // Solve mybatis page foreach parameter failure start
            if (Reflections.getFieldValue(boundSql, "metaParameters") != null) {
                MetaObject mo = (MetaObject) Reflections.getFieldValue(
                        boundSql, "metaParameters");
                Reflections.setFieldValue(newBoundSql, "metaParameters", mo);
            }
            // Solve the failure of mybatis page foreach parameter end
            MappedStatement newMs = copyFromMappedStatement(mappedStatement,
                    new BoundSqlSqlSource(newBoundSql));

            invocation.getArgs()[0] = newMs;
        }
        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {

    }

    private MappedStatement copyFromMappedStatement(MappedStatement ms,
                                                    SqlSource newSqlSource) {
        MappedStatement.Builder builder = new MappedStatement.Builder(
                ms.getConfiguration(), ms.getId(), newSqlSource,
                ms.getSqlCommandType());
        builder.resource(ms.getResource());
        builder.fetchSize(ms.getFetchSize());
        builder.statementType(ms.getStatementType());
        builder.keyGenerator(ms.getKeyGenerator());
        if (ms.getKeyProperties() != null) {
            for (String keyProperty : ms.getKeyProperties()) {
                builder.keyProperty(keyProperty);
            }
        }
        builder.timeout(ms.getTimeout());
        builder.parameterMap(ms.getParameterMap());
        builder.resultMaps(ms.getResultMaps());
        builder.cache(ms.getCache());
        return builder.build();
    }

    public static class BoundSqlSqlSource implements SqlSource {
        BoundSql boundSql;

        public BoundSqlSqlSource(BoundSql boundSql) {
            this.boundSql = boundSql;
        }

        public BoundSql getBoundSql(Object parameterObject) {
            return boundSql;
        }
    }
}
