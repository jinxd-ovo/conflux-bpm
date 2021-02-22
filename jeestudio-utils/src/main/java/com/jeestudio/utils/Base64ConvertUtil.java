package com.jeestudio.utils;

import java.io.UnsupportedEncodingException;
import java.util.Base64;

/**
 * @Description: Base64 convert util
 * @author: whl
 * @Date: 2019-12-03
 */
public class Base64ConvertUtil {

    public static String encode(String str) throws UnsupportedEncodingException {
        byte[] encodeBytes = Base64.getEncoder().encode(str.getBytes("utf-8"));
        return new String(encodeBytes);
    }

    public static String decode(String str) throws UnsupportedEncodingException {
        byte[] decodeBytes = Base64.getDecoder().decode(str.getBytes("utf-8"));
        return new String(decodeBytes);
    }
}
