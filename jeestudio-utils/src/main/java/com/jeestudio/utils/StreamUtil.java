package com.jeestudio.utils;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * @Description: Stream util
 * @author: whl
 * @Date: 2019-11-20
 */
public class StreamUtil {

    private static Logger logger = LoggerFactory.getLogger(StreamUtil.class);

    final static int BUFFER_SIZE = 4096;

    /**
     * Convert InputStream to string
     */
    public static String InputStreamTOString(InputStream in) {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] data = new byte[BUFFER_SIZE];
        String string = null;
        int count = 0;
        try {
            while ((count = in.read(data, 0, BUFFER_SIZE)) != -1) {
                outStream.write(data, 0, count);
            }
        } catch (IOException e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        }
        data = null;
        try {
            string = new String(outStream.toByteArray(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        }
        return string;
    }

    /**
     * Converting an InputStream to a character encoded string
     */
    public static String InputStreamTOString(InputStream in, String encoding) {
        String string = null;
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] data = new byte[BUFFER_SIZE];
        int count = -1;
        try {
            while ((count = in.read(data, 0, BUFFER_SIZE)) != -1) {
                outStream.write(data, 0, count);
            }
        } catch (IOException e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        }
        data = null;
        try {
            string = new String(outStream.toByteArray(), encoding);
        } catch (UnsupportedEncodingException e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        }
        return string;
    }

    /**
     * Convert string to InputStream
     */
    public static InputStream StringTOInputStream(String in) throws Exception {
        ByteArrayInputStream is = new ByteArrayInputStream(in.getBytes("UTF-8"));
        return is;
    }

    /**
     * Convert string to InputStream
     */
    public static byte[] StringTObyte(String in) {
        byte[] bytes = null;
        try {
            bytes = InputStreamTOByte(StringTOInputStream(in));
        } catch (IOException e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        }
        return bytes;
    }

    /**
     * Convert InputStream to byte array
     */
    public static byte[] InputStreamTOByte(InputStream in) throws IOException {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] data = new byte[BUFFER_SIZE];
        int count = -1;
        while ((count = in.read(data, 0, BUFFER_SIZE)) != -1) outStream.write(data, 0, count);
        data = null;
        return outStream.toByteArray();
    }

    /**
     * Convert byte array to InputStream
     */
    public static InputStream byteTOInputStream(byte[] in) throws Exception {
        ByteArrayInputStream is = new ByteArrayInputStream(in);
        return is;
    }

    /**
     * Convert byte array to string
     */
    public static String byteTOString(byte[] in) {
        InputStream is = null;
        try {
            is = byteTOInputStream(in);
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        }
        return InputStreamTOString(is, "UTF-8");
    }

    /**
     * Convert byte array to string
     *
     * @param in
     * @throws Exception
     */
    public static String getString(String in) {
        String is = null;
        try {
            is = byteTOString(StringTObyte(in));
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        }
        return is;
    }

    public byte[] getBytes(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] b = new byte[BUFFER_SIZE];
        int len = 0;
        while ((len = is.read(b, 0, BUFFER_SIZE)) != -1) {
            baos.write(b, 0, len);
        }
        baos.flush();
        byte[] bytes = baos.toByteArray();
        return bytes;
    }

    /**
     * Create file input stream processing based on file path
     */
    public static FileInputStream getFileInputStream(String filepath) {
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(filepath);
        } catch (FileNotFoundException e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        }
        return fileInputStream;
    }

    /**
     * Create a file input stream from a file object
     */
    public static FileInputStream getFileInputStream(File file) {
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        }
        return fileInputStream;
    }

    /**
     * Create a file output stream from a file object
     */
    public static FileOutputStream getFileOutputStream(File file, boolean append) {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(file, append);
        } catch (FileNotFoundException e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        }
        return fileOutputStream;
    }

    /**
     * Create file output stream processing based on file path
     */
    public static FileOutputStream getFileOutputStream(String filepath, boolean append) {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(filepath, append);
        } catch (FileNotFoundException e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        }
        return fileOutputStream;
    }

    public static File getFile(String filepath) {
        return new File(filepath);
    }

    public static ByteArrayOutputStream getByteArrayOutputStream() {
        return new ByteArrayOutputStream();
    }
}
