package com.jeestudio.utils;

/**
 * @Description: Validate password
 * @author: whl
 * @Date: 2019-12-11
 */
public class ValidatePassword {

    public static final String HASH_ALGORITHM = "SHA-1";
    public static final int HASH_INTERATIONS = 1024;
    public static final int SALT_SIZE = 8;

    public  static boolean validateUserPassword(String plainPassword, String password) {
        byte[] salt = Encodes.decodeHex(password.substring(0, 16));
        byte[] hashPassword = Digest.sha1(plainPassword.getBytes(), salt,
                HASH_INTERATIONS);
        return password.equals(Encodes.encodeHex(salt)
                + Encodes.encodeHex(hashPassword));
    }
}
