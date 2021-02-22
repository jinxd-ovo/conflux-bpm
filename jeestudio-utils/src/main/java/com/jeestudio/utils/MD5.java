package com.jeestudio.utils;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @Description: Jwt util
 * @author: whl
 * @Date: 2019-12-05
 */
public class MD5 {

    private static final Logger logger = LoggerFactory.getLogger(MD5.class);

    private static final char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd',
            'e', 'f'};

    public static String md5(String input) throws UnsupportedEncodingException {
        if (input == null) {
            return null;
        }
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            byte[] inputByteArray = input.getBytes("utf-8");
            messageDigest.update(inputByteArray);
            byte[] resultByteArray = messageDigest.digest();
            return byteArrayToHex(resultByteArray);
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    public static String md5(File file) {
        FileInputStream in = null;
        try {
            if (false == file.isFile()) {
                return null;
            }
            in = new FileInputStream(file);
            String result = md5(in);
            return result;
        } catch (FileNotFoundException e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (Exception err) {
                    logger.error(ExceptionUtils.getStackTrace(err));
                }
            }
        }
        return null;
    }

    public static String md5(InputStream in) {
        try {
            MessageDigest messagedigest = MessageDigest.getInstance("MD5");
            byte[] buffer = new byte[1024];
            int read = 0;
            while ((read = in.read(buffer)) != -1) {
                messagedigest.update(buffer, 0, read);
            }
            String result = byteArrayToHex(messagedigest.digest());
            return result;
        } catch (NoSuchAlgorithmException e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        } catch (FileNotFoundException e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        } catch (IOException e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (Exception err) {
                    logger.error(ExceptionUtils.getStackTrace(err));
                }
            }
        }
        return null;
    }

    private static String byteArrayToHex(byte[] byteArray) {
        char[] resultCharArray = new char[byteArray.length * 2];
        int index = 0;
        for (byte b : byteArray) {
            resultCharArray[index++] = hexDigits[b >>> 4 & 0xf];
            resultCharArray[index++] = hexDigits[b & 0xf];
        }
        return new String(resultCharArray);
    }
}
