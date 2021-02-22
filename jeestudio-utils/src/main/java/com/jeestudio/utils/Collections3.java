package com.jeestudio.utils;

import org.apache.commons.beanutils.PropertyUtils;

import java.util.*;

/**
 * @Description: Collections util
 * @author: whl
 * @author: houxl
 * @Date: 2019-11-29
 */
public class Collections3 {

    /**
     * Extract two attributes of objects in the collection (through getter function) and combine them into map
     */
    public static Map extractToMap(final Collection collection, final String keyPropertyName, final String valuePropertyName) {
        Map map = new HashMap(collection.size());
        try {
            for (Object obj : collection) {
                map.put(PropertyUtils.getProperty(obj, keyPropertyName),
                        PropertyUtils.getProperty(obj, valuePropertyName));
            }
        } catch (Exception e) {
            throw Reflections.convertReflectionExceptionToUnchecked(e);
        }
        return map;
    }

    /**
     * Extract an attribute of the object in the collection (through the getter function) and combine it into a list
     */
    public static List extractToList(final Collection collection, final String propertyName) {
        List list = new ArrayList(collection.size());
        try {
            for (Object obj : collection) {
                list.add(PropertyUtils.getProperty(obj, propertyName));
            }
        } catch (Exception e) {
            throw Reflections.convertReflectionExceptionToUnchecked(e);
        }
        return list;
    }

    /**
     * Extract an attribute of an object in the collection (through the getter function) and combine it into a string separated by a separator
     */
    public static String extractToString(final Collection collection, final String propertyName, final String separator) {
        List list = extractToList(collection, propertyName);
        return StringUtil.join(list, separator);
    }

    /**
     * Convert all elements of collection (through tostring()) to string, separated by separator.
     */
    public static String convertToString(final Collection collection, final String separator) {
        return StringUtil.join(collection, separator);
    }

    /**
     * Convert all elements of the collection (through tostring()) to string. Prefix is added in front of each element, and postfix is added later，as <div>mymessage</div>。
     */
    public static String convertToString(final Collection collection, final String prefix, final String postfix) {
        StringBuilder builder = new StringBuilder();
        for (Object o : collection) {
            builder.append(prefix).append(o).append(postfix);
        }
        return builder.toString();
    }

    /**
     * Judge whether it is empty
     */
    public static boolean isEmpty(Collection collection) {
        return (collection == null || collection.isEmpty());
    }

    /**
     * Gets the first element of the collection, and returns NULL if the collection is empty
     */
    public static <T> T getFirst(Collection<T> collection) {
        if (isEmpty(collection)) {
            return null;
        }
        return collection.iterator().next();
    }

    /**
     * Gets the last element of the collection, and returns NULL if the collection is empty
     */
    public static <T> T getLast(Collection<T> collection) {
        if (isEmpty(collection)) {
            return null;
        }

        //When the type is list, get the last element directly.
        if (collection instanceof List) {
            List<T> list = (List<T>) collection;
            return list.get(list.size() - 1);
        }

        //Other types scroll through iterator to the last element
        Iterator<T> iterator = collection.iterator();
        while (true) {
            T current = iterator.next();
            if (!iterator.hasNext()) {
                return current;
            }
        }
    }

    /**
     * Returns a + b's new list
     */
    public static <T> List<T> union(final Collection<T> a, final Collection<T> b) {
        List<T> result = new ArrayList<T>(a);
        result.addAll(b);
        return result;
    }

    /**
     * Returns a - b's new list
     */
    public static <T> List<T> subtract(final Collection<T> a, final Collection<T> b) {
        List<T> list = new ArrayList<T>(a);
        for (T element : b) {
            list.remove(element);
        }
        return list;
    }

    /**
     * Returns a new list of the intersection of a and b
     */
    public static <T> List<T> intersection(Collection<T> a, Collection<T> b) {
        List<T> list = new ArrayList<T>();
        for (T element : a) {
            if (b.contains(element)) {
                list.add(element);
            }
        }
        return list;
    }
}
