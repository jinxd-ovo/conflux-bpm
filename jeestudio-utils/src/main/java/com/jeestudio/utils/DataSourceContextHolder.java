package com.jeestudio.utils;

/**
 * @Description: DataSourceContextHolder
 * @author: whl
 * @Date: 2020-02-11
 */
public class DataSourceContextHolder {

    private static final ThreadLocal<String> contextHolder = new ThreadLocal<String>();
    public static final String DEFAULT_DBTYPE = "default";

    public static void setDbType(String dbType) {
        contextHolder.set(dbType);
    }

    public static String getDbType() {
        String dataSource = contextHolder.get();
        if (StringUtil.isEmpty(dataSource)) {
            return DEFAULT_DBTYPE;
        } else {
            return dataSource;
        }
    }

    public static void clearDbType() {
        contextHolder.remove();
    }
}
