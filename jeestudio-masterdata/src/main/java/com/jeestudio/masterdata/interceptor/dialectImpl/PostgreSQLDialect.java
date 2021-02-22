package com.jeestudio.masterdata.interceptor.dialectImpl;

import com.jeestudio.masterdata.interceptor.Dialect;

/**
 * @Description: Implementation of PostgreSQL's pagination database dialect
 * @author: whl
 * @Date: 2019-11-28
 */
public class PostgreSQLDialect implements Dialect {

    public boolean supportsLimit() {
        return true;
    }

    @Override
    public String getLimitString(String sql, int offset, int limit) {
        return getLimitString(sql, offset, Integer.toString(offset),
                Integer.toString(limit));
    }

    /**
     * Turn SQL into paging SQL statement, and provide to replace offset and limit with placeholder
     * <pre>
     * as mysql
     * dialect.getLimitString("select * from user", 12, ":offset",0,":limit") Will return
     * select * from user limit :offset,:limit
     * </pre>
     *
     * @param sql               Actual SQL statement
     * @param offset            Number of page start records
     * @param offsetPlaceholder Page start record number - placeholder
     * @param limitPlaceholder  Number of paging records occupying sign
     * @return Paging SQL with placeholders
     */
    public String getLimitString(String sql, int offset,
                                 String offsetPlaceholder, String limitPlaceholder) {
        StringBuilder pageSql = new StringBuilder().append(sql);
        pageSql = offset <= 0
                ? pageSql.append(" limit ").append(limitPlaceholder) :
                pageSql.append(" limit ").append(limitPlaceholder).append(" offset ").append(offsetPlaceholder);
        return pageSql.toString();
    }
}
