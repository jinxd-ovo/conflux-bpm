package com.jeestudio.common.utils;

import org.apache.commons.beanutils.Converter;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Description: Date converter
 * @author: houxl
 * @Date: 2020-01-09
 */
public class DateConverter implements Converter {

    private static final Logger logger = LoggerFactory.getLogger(DateConverter.class);

    private static final String DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    private static final String DATETIME_PATTERN_NO_SECOND = "yyyy-MM-dd HH:mm";
    private static final String DATE_PATTERN = "yyyy-MM-dd";
    private static final String MONTH_PATTERN = "yyyy-MM";

    /**
     * Convert between date and string
     * @param type Date.class or String.class
     * @param value
     * @return string while type is Date.class, date while type is String.class.
     */
    public Object convert(Class type, Object value) {
        Object result = null;
        if (type == Date.class) {
            try {
                result = doConvertToDate(value);
            } catch (ParseException e) {
                logger.error(ExceptionUtils.getStackTrace(e));
            }
        } else if (type == String.class) {
            result = doConvertToString(value);
        }
        return result;
    }

    /**
     * Convert string to date
     */
    private Date doConvertToDate(Object value) throws ParseException {
        Date result = null;
        if (value instanceof String) {
            result = DateUtils.parseDate((String) value, new String[] { DATE_PATTERN, DATETIME_PATTERN,
                    DATETIME_PATTERN_NO_SECOND, MONTH_PATTERN });
            //All patterns failed, try a milliseconds constructor
            if (result == null && StringUtils.isNotEmpty((String) value)) {
                try {
                    result = new Date(new Long((String) value).longValue());
                } catch (Exception e) {
                    logger.error(ExceptionUtils.getStackTrace(e));
                }
            }
        } else if (value instanceof Object[]) {
            Object[] array = (Object[]) value;
            if (array.length >= 1) {
                value = array[0];
                result = doConvertToDate(value);
            }
        } else if (Date.class.isAssignableFrom(value.getClass())) {
            result = (Date) value;
        }
        return result;
    }

    /**
     * Convert date to string
     */
    private String doConvertToString(Object value) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATETIME_PATTERN);
        String result = null;
        if (value instanceof Date) {
            result = simpleDateFormat.format(value);
        }
        return result;
    }
}
