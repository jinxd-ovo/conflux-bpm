package com.jeestudio.utils;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.util.Properties;

/**
 * @Description: Email util
 * @author: whl
 * @Date: 2019-11-20
 */
public class EmailUtil {

    private static Logger logger = LoggerFactory.getLogger(EmailUtil.class);

    private static JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

    private static void mailInit(String host, int port, String userName, String password) {
        mailSender.setHost(host);
        mailSender.setPort(port);
        mailSender.setUsername(userName);
        mailSender.setPassword(password);
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", true);
        properties.put("mail.smtp.ssl.enable", true);
        properties.put("mail.smtp.starttls.enable", true);
        properties.put("mail.smtp.timeout", 5000);
        mailSender.setJavaMailProperties(properties);
    }

    /**
     * Send email
     */
    public static void easyEmail(String host, int port, String userName, String password, String from, String to, String title, String content) {
        mailInit(host, port, userName, password);
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
        try {
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(title);
            helper.setText(content);
        } catch (MessagingException e) {
            logger.error("Failed to send email, " + ExceptionUtils.getStackTrace(e));
        }
        mailSender.send(mimeMessage);
    }

    /**
     * Send html email
     */
    public static void htmlEmail(String host, int port, String userName, String password, String from, String to, String title, String content) {
        mailInit(host, port, userName, password);
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");
        try {
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(title);
            helper.setText(content, true);
        } catch (MessagingException e) {
            logger.error("Failed to send html email, " + ExceptionUtils.getStackTrace(e));
        }
        mailSender.send(mimeMessage);
    }

    /**
     * Send email with attachment
     */
    public static void fileEmail(String host, int port, String userName, String password, String from, String to, String title, String content, String fileName, File file) {
        mailInit(host, port, userName, password);
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = null;
        try {
            helper = new MimeMessageHelper(mimeMessage, true);
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(title);
            helper.setText(content);
            helper.addAttachment(fileName, file);
        } catch (MessagingException e) {
            logger.error("Failed to send email with attachment, " + ExceptionUtils.getStackTrace(e));
        }
        mailSender.send(mimeMessage);
    }
}

