package com.jeestudio.common.mapper;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.namespace.QName;

import com.jeestudio.common.utils.DateConverter;
import com.jeestudio.utils.Reflections;
import com.jeestudio.utils.StringUtil;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import com.jeestudio.utils.Exceptions;

/**
 * @Description: Jaxb mapper
 * @author: houxl
 * @Date: 2020-01-19
 */
public class JaxbMapper {

    private static final Logger logger = LoggerFactory.getLogger(DateConverter.class);
    private static ConcurrentMap<Class, JAXBContext> jaxbContexts = new ConcurrentHashMap<Class, JAXBContext>();

    /**
     * Java Object->Xml without encoding
     */
    public static String toXml(Object root) {
        Class clazz = Reflections.getUserClass(root);
        return toXml(root, clazz, null);
    }

    /**
     * Java Object->Xml with encoding
     */
    public static String toXml(Object root, String encoding) {
        Class clazz = Reflections.getUserClass(root);
        return toXml(root, clazz, encoding);
    }

    /**
     * Java Object->Xml with encoding
     */
    public static String toXml(Object root, Class clazz, String encoding) {
        try {
            StringWriter writer = new StringWriter();
            createMarshaller(clazz, encoding).marshal(root, writer);
            return writer.toString();
        } catch (JAXBException e) {
            throw Exceptions.unchecked(e);
        }
    }

    /**
     * Java Collection->Xml without encoding, support Root Element is Collection.
     */
    public static String toXml(Collection<?> root, String rootName, Class clazz) {
        return toXml(root, rootName, clazz, null);
    }

    /**
     * Java Collection->Xml with encoding, support Root Element is Collection.
     */
    public static String toXml(Collection<?> root, String rootName, Class clazz, String encoding) {
        try {
            CollectionWrapper wrapper = new CollectionWrapper();
            wrapper.collection = root;
            JAXBElement<CollectionWrapper> wrapperElement = new JAXBElement<CollectionWrapper>(new QName(rootName),
                    CollectionWrapper.class, wrapper);
            StringWriter writer = new StringWriter();
            createMarshaller(clazz, encoding).marshal(wrapperElement, writer);
            return writer.toString();
        } catch (JAXBException e) {
            logger.error("Error while doing toXml, " + ExceptionUtils.getStackTrace(e));
            return null;
        }
    }

    /**
     * Xml->Java Object
     */
    @SuppressWarnings("unchecked")
    public static <T> T fromXml(String xml, Class<T> clazz) {
        try {
            StringReader reader = new StringReader(xml);
            return (T) createUnmarshaller(clazz).unmarshal(reader);
        } catch (JAXBException e) {
            logger.error("Error while doing fromXml, " + ExceptionUtils.getStackTrace(e));
            return null;
        }
    }

    /**
     * Create Marshaller and set encoding(nullable)
     */
    public static Marshaller createMarshaller(Class clazz, String encoding) {
        try {
            JAXBContext jaxbContext = getJaxbContext(clazz);
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            if (StringUtil.isNotBlank(encoding)) {
                marshaller.setProperty(Marshaller.JAXB_ENCODING, encoding);
            }
            return marshaller;
        } catch (JAXBException e) {
            logger.error("Error while doing createMarshaller, " + ExceptionUtils.getStackTrace(e));
            return null;
        }
    }

    /**
     * Create UnMarshaller
     */
    public static Unmarshaller createUnmarshaller(Class clazz) {
        try {
            JAXBContext jaxbContext = getJaxbContext(clazz);
            return jaxbContext.createUnmarshaller();
        } catch (JAXBException e) {
            logger.error("Error while doing createUnmarshaller, " + ExceptionUtils.getStackTrace(e));
            return null;
        }
    }

    /**
     * Get JaxbContext
     */
    protected static JAXBContext getJaxbContext(Class clazz) {
        Assert.notNull(clazz, "'clazz' must not be null");
        JAXBContext jaxbContext = jaxbContexts.get(clazz);
        if (jaxbContext == null) {
            try {
                jaxbContext = JAXBContext.newInstance(clazz, CollectionWrapper.class);
                jaxbContexts.putIfAbsent(clazz, jaxbContext);
            } catch (JAXBException e) {
                logger.error("Could not instantiate JAXBContext for class [" + clazz + "], " + ExceptionUtils.getStackTrace(e));
                return null;
            }
        }
        return jaxbContext;
    }

    /**
     * Wrapper when Root Element is Collection
     */
    public static class CollectionWrapper {

        @XmlAnyElement
        protected Collection<?> collection;
    }
}
