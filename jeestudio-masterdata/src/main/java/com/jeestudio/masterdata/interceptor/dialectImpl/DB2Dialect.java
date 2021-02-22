package com.jeestudio.masterdata.interceptor.dialectImpl;

import com.jeestudio.masterdata.interceptor.Dialect;

/**
 * @Description: Implementation of DB2's pagination database dialect
 * @author: whl
 * @Date: 2019-11-28
 */
public class DB2Dialect implements Dialect {
    @Override
    public boolean supportsLimit() {
        return true;
    }

    private static String getRowNumber(String sql) {
        StringBuilder rownumber = new StringBuilder(50)
                .append("rownumber() over(");

        int orderByIndex = sql.toLowerCase().indexOf("order by");

        if (orderByIndex > 0 && !hasDistinct(sql)) {
            rownumber.append(sql.substring(orderByIndex));
        }

        rownumber.append(") as rownumber_,");

        return rownumber.toString();
    }

    private static boolean hasDistinct(String sql) {
        return sql.toLowerCase().contains("select distinct");
    }

    @Override
    public String getLimitString(String sql, int offset, int limit) {
        return getLimitString(sql, offset, Integer.toString(offset), Integer.toString(limit));
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
        int startOfSelect = sql.toLowerCase().indexOf("select");

        StringBuilder pagingSelect = new StringBuilder(sql.length() + 100)
                .append(sql.substring(0, startOfSelect)) //add the comment
                .append("select * from ( select ") //nest the main query in an outer select
                .append(getRowNumber(sql)); //add the rownnumber bit into the outer query select list

        if (hasDistinct(sql)) {
            pagingSelect.append(" row_.* from ( ") //add another (inner) nested select
                    .append(sql.substring(startOfSelect)) //add the main query
                    .append(" ) as row_"); //close off the inner nested select
        } else {
            pagingSelect.append(sql.substring(startOfSelect + 6)); //add the main query
        }

        pagingSelect.append(" ) as temp_ where rownumber_ ");

        if (offset > 0) {
            String endString = offsetPlaceholder + "+" + limitPlaceholder;
            pagingSelect.append("between ").append(offsetPlaceholder)
                    .append("+1 and ").append(endString);
        } else {
            pagingSelect.append("<= ").append(limitPlaceholder);
        }

        return pagingSelect.toString();
    }
}