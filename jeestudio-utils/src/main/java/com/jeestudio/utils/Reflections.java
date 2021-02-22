package com.jeestudio.utils;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.lang.reflect.*;

/**
 * @Description: Reflection tools
 * @author: whl
 * @Date: 2019-11-21
 */
public class Reflections {

    private static Logger logger = LoggerFactory.getLogger(Reflections.class);

    private static final String SETTER_PREFIX = "set";
    private static final String GETTER_PREFIX = "get";
    private static final String CGLIB_CLASS_SEPARATOR = "$$";

    /**
     * Call getter method
     *
     * @param obj multiple levels are supported, such as object name, object name and method.
     */
    public static Object invokeGetter(Object obj, String propertyName) {
        Object object = obj;
        for (String name : StringUtils.split(propertyName, ".")) {
            String getterMethodName = GETTER_PREFIX + StringUtils.capitalize(name);
            object = invokeMethod(object, getterMethodName, new Class[]{}, new Object[]{});
        }
        return object;
    }

    /**
     * Call setter method, match only method name
     *
     * @param obj          multiple levels are supported, such as object name, object name and method
     * @param propertyName
     * @param value
     */
    public static void invokeSetter(Object obj, String propertyName, Object value) {
        Object object = obj;
        String[] names = StringUtils.split(propertyName, ".");
        for (int i = 0; i < names.length; i++) {
            if (i < names.length - 1) {
                String getterMethodName = GETTER_PREFIX + StringUtils.capitalize(names[i]);
                object = invokeMethod(object, getterMethodName, new Class[]{}, new Object[]{});
            } else {
                String setterMethodName = SETTER_PREFIX + StringUtils.capitalize(names[i]);
                invokeMethodByName(object, setterMethodName, new Object[]{value});
            }
        }
    }

    /**
     * Read the property value of the object directly, ignore the private / protected modifier, and do not go through the getter function.
     */
    public static Object getFieldValue(final Object obj, final String fieldName) {
        Field field = getAccessibleField(obj, fieldName);
        if (field == null) {
            throw new IllegalArgumentException("Could not find field [" + fieldName + "] on target [" + obj + "]");
        }
        Object result = null;
        try {
            result = field.get(obj);
        } catch (IllegalAccessException e) {
            logger.error("Error while getting field value, " + ExceptionUtils.getStackTrace(e));
        }
        return result;
    }

    /**
     * Set the property value of the object directly, ignore the private / protected modifier, and do not go through the setter function.
     */
    public static void setFieldValue(final Object obj, final String fieldName, final Object value) {
        Field field = getAccessibleField(obj, fieldName);
        if (field == null) {
            throw new IllegalArgumentException("Could not find field [" + fieldName + "] on target [" + obj + "]");
        }
        try {
            field.set(obj, value);
        } catch (IllegalAccessException e) {
            logger.error("Error while setting field value, " + ExceptionUtils.getStackTrace(e));
        }
    }

    /**
     * Call object methods directly, ignoring the private / protected modifier It is used in the case of one-time call,
     * otherwise the getaccessiblemethod() function should be used to get the method and call it repeatedly Match method name + parameter type at the same time
     */
    public static Object invokeMethod(final Object obj, final String methodName, final Class<?>[] parameterTypes,
                                      final Object[] args) {
        Method method = getAccessibleMethod(obj, methodName, parameterTypes);
        if (method == null) {
            throw new IllegalArgumentException("Could not find method [" + methodName + "] on target [" + obj + "]");
        }
        try {
            return method.invoke(obj, args);
        } catch (Exception e) {
            throw convertReflectionExceptionToUnchecked(e);
        }
    }

    /**
     * Call object methods directly, ignoring the private / protected modifier It is used in the case of one-time call,
     * otherwise it should be repeatedly called after getting method by using getaccessiblemethodbyname() function Only match the function name, if there are multiple function calls with the same name
     */
    public static Object invokeMethodByName(final Object obj, final String methodName, final Object[] args) {
        Method method = getAccessibleMethodByName(obj, methodName);
        if (method == null) {
            throw new IllegalArgumentException("Could not find method [" + methodName + "] on target [" + obj + "]");
        }
        try {
            return method.invoke(obj, args);
        } catch (Exception e) {
            throw convertReflectionExceptionToUnchecked(e);
        }
    }

    /**
     * Loop up transformation to obtain the declaredfield of the object and force it to be accessible
     */
    public static Field getAccessibleField(final Object obj, final String fieldName) {
        Validate.notNull(obj, "object can't be null");
        Validate.notBlank(fieldName, "fieldName can't be blank");
        for (Class<?> superClass = obj.getClass(); superClass != Object.class; superClass = superClass.getSuperclass()) {
            try {
                Field field = superClass.getDeclaredField(fieldName);
                makeAccessible(field);
                return field;
            } catch (NoSuchFieldException e) {
                //logger.warn("No such field exception, " + ExceptionUtils.getStackTrace(e));
                continue;
            }
        }
        return null;
    }

    /**
     * Loop up transformation, obtain the declaredmethod of the object and force it to be accessible
     */
    public static Method getAccessibleMethod(final Object obj, final String methodName,
                                             final Class<?>... parameterTypes) {
        Validate.notNull(obj, "object can't be null");
        Validate.notBlank(methodName, "methodName can't be blank");
        for (Class<?> searchType = obj.getClass(); searchType != Object.class; searchType = searchType.getSuperclass()) {
            try {
                Method method = searchType.getDeclaredMethod(methodName, parameterTypes);
                makeAccessible(method);
                return method;
            } catch (NoSuchMethodException e) {
                logger.warn("No such method exception, " + ExceptionUtils.getStackTrace(e));
                continue;
            }
        }
        return null;
    }

    /**
     * Loop up transformation, obtain the declaredmethod of the object and force it to be accessible
     */
    public static Method getAccessibleMethodByName(final Object obj, final String methodName) {
        Validate.notNull(obj, "object can't be null");
        Validate.notBlank(methodName, "methodName can't be blank");
        for (Class<?> searchType = obj.getClass(); searchType != Object.class; searchType = searchType.getSuperclass()) {
            Method[] methods = searchType.getDeclaredMethods();
            for (Method method : methods) {
                if (method.getName().equals(methodName)) {
                    makeAccessible(method);
                    return method;
                }
            }
        }
        return null;
    }

    /**
     * Change the private / protected method to public, try not to call the actual changed statement,
     * and avoid the security manager complaint of JDK.
     */
    public static void makeAccessible(Method method) {
        if ((false == Modifier.isPublic(method.getModifiers())
                || false == Modifier.isPublic(method.getDeclaringClass().getModifiers()))
                && false == method.isAccessible()) {
            method.setAccessible(true);
        }
    }

    /**
     * Change the member variable of private / protected to public, try not to call the actual changed statement,
     * and avoid the complaints of security manager of JDK.
     *
     * @param field
     */
    public static void makeAccessible(Field field) {
        if ((false == Modifier.isPublic(field.getModifiers())
                || false == Modifier.isPublic(field.getDeclaringClass().getModifiers())
                || Modifier.isFinal(field.getModifiers()))
                && false == field.isAccessible()) {
            field.setAccessible(true);
        }
    }

    /**
     * Obtain the type of generic parameter declared in the class definition through reflection.
     * Note that the generic must be defined at the parent class.
     */
    @SuppressWarnings("unchecked")
    public static <T> Class<T> getClassGenricType(final Class clazz) {
        return getClassGenricType(clazz, 0);
    }

    /**
     * By reflection, get the type of generic parameter of the parent class declared in the class definition
     */
    public static Class getClassGenricType(final Class clazz, final int index) {
        Type genType = clazz.getGenericSuperclass();
        if (false == (genType instanceof ParameterizedType)) {
            logger.warn(clazz.getSimpleName() + "'s superclass is not ParameterizedType");
            return Object.class;
        }
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        if (index >= params.length || index < 0) {
            logger.warn("Index: " + index + ", Size of " + clazz.getSimpleName() + "'s Parameterized Type: "
                    + params.length);
            return Object.class;
        }
        if (!(params[index] instanceof Class)) {
            logger.warn(clazz.getSimpleName() + " not set the actual class on superclass generic parameter");
            return Object.class;
        }
        return (Class) params[index];
    }

    public static Class<?> getUserClass(Object instance) {
        Assert.notNull(instance, "Instance must not be null");
        Class clazz = instance.getClass();
        if (clazz != null && clazz.getName().contains(CGLIB_CLASS_SEPARATOR)) {
            Class<?> superClass = clazz.getSuperclass();
            if (superClass != null && !Object.class.equals(superClass)) {
                return superClass;
            }
        }
        return clazz;
    }

    /**
     * Convert checked exception in reflection to unchecked exception
     */
    public static RuntimeException convertReflectionExceptionToUnchecked(Exception e) {
        if (e instanceof IllegalAccessException || e instanceof IllegalArgumentException
                || e instanceof NoSuchMethodException) {
            return new IllegalArgumentException(e);
        } else if (e instanceof InvocationTargetException) {
            return new RuntimeException(((InvocationTargetException) e).getTargetException());
        } else if (e instanceof RuntimeException) {
            return (RuntimeException) e;
        }
        return new RuntimeException("Unexpected Checked Exception.", e);
    }

    /**
     * Get field from a class or its parent class
     */
    public static Field getThisField(Class clazz, String javaField) {
        Field field = null;
        try {
            field = clazz.getDeclaredField(javaField);
        } catch (NoSuchFieldException e) {
            clazz = clazz.getSuperclass();
            try {
                field = clazz.getDeclaredField(javaField);
            } catch (NoSuchFieldException ee) {
                clazz = clazz.getSuperclass();
                try {
                    field = clazz.getDeclaredField(javaField);
                } catch (NoSuchFieldException eee) {
                    clazz = clazz.getSuperclass();
                    try {
                        field = clazz.getDeclaredField(javaField);
                    } catch (NoSuchFieldException eeee) {
                    }
                }
            }
        }
        return field;
    }
}