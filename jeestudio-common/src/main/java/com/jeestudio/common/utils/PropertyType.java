package com.jeestudio.common.utils;

import java.util.Date;

/**
 * @Description: Property type
 * @author: houxl
 * @Date: 2020-01-09
 */
public enum PropertyType {

    S(String.class),
    I(Integer.class),
    L(Long.class),
    F(Float.class),
    N(Double.class),
    D(Date.class),
    SD(java.sql.Date.class),
    B(Boolean.class);

    private Class<?> clazz;

    private PropertyType(Class<?> clazz) {
        this.clazz = clazz;
    }

    public Class<?> getValue() {
        return clazz;
    }
}
