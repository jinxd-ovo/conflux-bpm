package com.jeestudio.masterdata.interceptor.dialectImpl;

import com.jeestudio.masterdata.interceptor.Dialect;

/**
 * @Description: Implementation of Sybase's pagination database dialect
 * @author: whl
 * @Date: 2019=11=28
 */
public class SybaseDialect implements Dialect {

    public boolean supportsLimit() {
        return false;
    }


    @Override
    public String getLimitString(String sql, int offset, int limit) {
        return null;
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
     * @param limit             Number of records per page
     * @param limitPlaceholder  Number of paging records occupying sign
     * @return Paging SQL with placeholders
     */
    public String getLimitString(String sql, int offset, String offsetPlaceholder, int limit, String limitPlaceholder) {
        throw new UnsupportedOperationException("paged queries not supported");
    }

}