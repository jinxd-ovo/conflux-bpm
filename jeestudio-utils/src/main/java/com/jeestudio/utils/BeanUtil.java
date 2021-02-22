package com.jeestudio.utils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaProperty;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Description: Bean replication util
 * @author houxl
 * @Date: 2019-12-03
 */
public class BeanUtil extends PropertyUtilsBean {

    private static Logger logger = LoggerFactory.getLogger(BeanUtil.class);

    public BeanUtil() {
        super();
    }

    private static void convert(Object dest, Object orig) {
        //Validate existence of the specified beans
        if (dest == null) {
            throw new IllegalArgumentException("No destination bean specified");
        }
        if (orig == null) {
            throw new IllegalArgumentException("No origin bean specified");
        }
        //Copy the properties, converting as necessary
        if (orig instanceof DynaBean) {
            DynaProperty origDescriptors[] = ((DynaBean) orig).getDynaClass().getDynaProperties();
            for (int i = 0; i < origDescriptors.length; i++) {
                String name = origDescriptors[i].getName();
                if (PropertyUtils.isWriteable(dest, name)) {
                    Object value = ((DynaBean) orig).get(name);
                    try {
                        getInstance().setSimpleProperty(dest, name, value);
                    } catch (Exception e) {
                        logger.warn("Warning while converting bean, " + ExceptionUtils.getStackTrace(e));
                    }
                }
            }
        } else if (orig instanceof Map) {
            Iterator names = ((Map) orig).keySet().iterator();
            while (names.hasNext()) {
                String name = (String) names.next();
                if (PropertyUtils.isWriteable(dest, name)) {
                    Object value = ((Map) orig).get(name);
                    try {
                        getInstance().setSimpleProperty(dest, name, value);
                    } catch (Exception e) {
                        logger.warn("Warning while converting bean, " + ExceptionUtils.getStackTrace(e));
                    }
                }
            }
        } else {
            //orig is a standard JavaBean)
            PropertyDescriptor origDescriptors[] = PropertyUtils.getPropertyDescriptors(orig);
            for (int i = 0; i < origDescriptors.length; i++) {
                String name = origDescriptors[i].getName();
                if ("class".equals(name)) {
                    continue; // No point in trying to set an object's class
                }
                if (PropertyUtils.isReadable(orig, name) && PropertyUtils.isWriteable(dest, name)) {
                    try {
                        Object value = PropertyUtils.getSimpleProperty(orig, name);
                        getInstance().setSimpleProperty(dest, name, value);
                    } catch (java.lang.IllegalArgumentException ie) {
                        logger.warn("Warning while converting bean, " + ExceptionUtils.getStackTrace(ie));
                    } catch (Exception e) {
                        logger.warn("Warning while converting bean, " + ExceptionUtils.getStackTrace(e));
                    }
                }
            }
        }
    }

    /**
     * Object copy, do not copy to the target object when the object is null.
     */
    public static void copyBeanNotNull2Bean(Object databean, Object tobean) {
        PropertyDescriptor origDescriptors[] = PropertyUtils.getPropertyDescriptors(databean);
        for (int i = 0; i < origDescriptors.length; i++) {
            String name = origDescriptors[i].getName();
            if ("class".equals(name)) {
                continue; // No point in trying to set an object's class
            }
            if (PropertyUtils.isReadable(databean, name)
                    && PropertyUtils.isWriteable(tobean, name)) {
                try {
                    Object value = PropertyUtils.getSimpleProperty(databean, name);
                    if (value != null) {
                        getInstance().setSimpleProperty(tobean, name, value);
                    }
                } catch (java.lang.IllegalArgumentException ie) {
                    logger.warn("Warning while copying bean without null, " + ExceptionUtils.getStackTrace(ie));
                } catch (Exception e) {
                    logger.warn("Warning while copying bean without null, " + ExceptionUtils.getStackTrace(e));
                }
            }
        }
    }

    /**
     * Object copy
     */
    public static void copyBean2Bean(Object dest, Object orig) {
        convert(dest, orig);
    }

    /**
     * Object copy to map
     */
    public static void copyBean2Map(Map map, Object bean) {
        PropertyDescriptor[] pds = PropertyUtils.getPropertyDescriptors(bean);
        for (int i = 0; i < pds.length; i++) {
            PropertyDescriptor pd = pds[i];
            String propname = pd.getName();
            try {
                Object propvalue = PropertyUtils.getSimpleProperty(bean,
                        propname);
                map.put(propname, propvalue);
            } catch (IllegalAccessException e) {
                logger.warn("Warning while copying bean to map, " + ExceptionUtils.getStackTrace(e));
            } catch (InvocationTargetException e) {
                logger.warn("Warning while copying bean to map, " + ExceptionUtils.getStackTrace(e));
            } catch (NoSuchMethodException e) {
                logger.warn("Warning while copying bean to map, " + ExceptionUtils.getStackTrace(e));
            }
        }
    }

    /**
     * Copy map to bean
     */
    public static void copyMap2Bean(Object bean, Map properties) {
        //Do nothing unless both arguments have been specified
        if ((bean == null) || (properties == null)) {
            return;
        }
        //Loop through the property name/value pairs to be set
        Iterator names = properties.keySet().iterator();
        while (names.hasNext()) {
            String name = (String) names.next();
            //Identify the property name and value(s) to be assigned
            if (name == null) {
                continue;
            }
            Object value = properties.get(name);
            try {
                Class clazz = PropertyUtils.getPropertyType(bean, name);
                if (null == clazz) {
                    continue;
                }
                String className = clazz.getName();
                if (className.equalsIgnoreCase("java.sql.Timestamp")) {
                    if (value == null || value.equals("")) {
                        continue;
                    }
                }
                getInstance().setSimpleProperty(bean, name, value);
            } catch (IllegalAccessException e) {
                logger.warn("Warning while copying map to bean, " + ExceptionUtils.getStackTrace(e));
            } catch (InvocationTargetException e) {
                logger.warn("Warning while copying map to bean, " + ExceptionUtils.getStackTrace(e));
            } catch (NoSuchMethodException e) {
                logger.warn("Warning while copying map to bean, " + ExceptionUtils.getStackTrace(e));
            }
        }
    }

    /**
     * Copy map to bean with null replaced by default value
     */
    public static void copyMap2Bean(Object bean, Map properties, String defaultValue) {
        //Do nothing unless both arguments have been specified
        if ((bean == null) || (properties == null)) {
            return;
        }
        //Loop through the property name/value pairs to be set
        Iterator names = properties.keySet().iterator();
        while (names.hasNext()) {
            String name = (String) names.next();
            //Identify the property name and value(s) to be assigned
            if (name == null) {
                continue;
            }
            Object value = properties.get(name);
            try {
                Class clazz = PropertyUtils.getPropertyType(bean, name);
                if (null == clazz) {
                    continue;
                }
                String className = clazz.getName();
                if (className.equalsIgnoreCase("java.sql.Timestamp")) {
                    if (value == null || value.equals("")) {
                        continue;
                    }
                }
                if (className.equalsIgnoreCase("java.lang.String")) {
                    if (value == null) {
                        value = defaultValue;
                    }
                }
                getInstance().setSimpleProperty(bean, name, value);
            } catch (IllegalAccessException e) {
                logger.warn("Warning while copying map to bean, " + ExceptionUtils.getStackTrace(e));
            } catch (InvocationTargetException e) {
                logger.warn("Warning while copying map to bean, " + ExceptionUtils.getStackTrace(e));
            } catch (NoSuchMethodException e) {
                logger.warn("Warning while copying map to bean, " + ExceptionUtils.getStackTrace(e));
            }
        }
    }
}
