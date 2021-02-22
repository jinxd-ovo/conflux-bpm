package com.jeestudio.datasource.interceptor.dialectImpl;

import com.jeestudio.datasource.interceptor.Dialect;

/**
 * @Description: Implementation of HSQL's pagination database dialect
 * @author: whl
 * @Date: 2019-11-28
 */
public class HSQLDialect implements Dialect {
    @Override
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
    public String getLimitString(String sql, int offset, String offsetPlaceholder, String limitPlaceholder) {
        boolean hasOffset = offset > 0;
        return
                new StringBuffer(sql.length() + 10)
                        .append(sql)
                        .insert(sql.toLowerCase().indexOf("select") + 6, hasOffset ? " limit " + offsetPlaceholder + " " + limitPlaceholder : " top " + limitPlaceholder)
                        .toString();
    }

}