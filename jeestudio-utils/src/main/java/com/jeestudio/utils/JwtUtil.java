package com.jeestudio.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @Description: Jwt util
 * @author: whl
 * @Date: 2019-12-05
 */
public class JwtUtil {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    /**
     * The information in token can be obtained without secret decryption
     */
    public static String getClaim(String token, String claim) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getClaim(claim).asString();
        } catch (JWTDecodeException e) {
            logger.error("Error while decoding token, " + ExceptionUtils.getStackTrace(e));
            return null;
        }
    }

    /**
     * Decrypt Token
     *
     * @param userName
     * @return
     */
    public static Map<String, Object> verify(String userName, String userId, String token) {
        String clientId = getClaim(token, "clientId");

        Map<String, Object> map = new HashMap<String, Object>();
        try {
            String secret = Base64ConvertUtil.encode(userName + userId);
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secret))
                    .withClaim("username", userName)
                    .withClaim("userId", userId)
                    .withClaim("clientId", clientId)
                    .build();
            DecodedJWT jwt = null;
            if (verifier != null) {
                jwt = verifier.verify(token);
            }
            map.put("isSuccess", true);
            map.put("exception", null);
        } catch (Exception ex) {
            logger.error("JWTToken Expire");
            map.put("isSuccess", false);
            map.put("exception", ex);
        }
        return map;
    }

    /**
     * Generate signature
     *
     * @param userName
     * @param userId
     * @return
     */
    public static String sign(String userName, String userId) {
        try {
            //Jwt authentication encryption private key
            String secret = Base64ConvertUtil.encode(userName + userId);
            Date current = new Date();
            Date date = new Date(System.currentTimeMillis() + 15 * 60 * 1000);
            Algorithm algorithm = Algorithm.HMAC256(secret);
            Map<String, Object> map = new HashMap<>();
            map.put("alg", "HS256");
            map.put("typ", "JWT");
            return JWT.create().withHeader(map)
                    .withClaim("username", userName)
                    .withClaim("userId", userId)
                    .withClaim("clientId", UUID.randomUUID().toString().replaceAll("-", ""))
                    .withClaim("iss", "Service")
                    .withClaim("aud", "APP")
                    .withIssuedAt(current)
                    .withExpiresAt(date)
                    .sign(algorithm);
        } catch (UnsupportedEncodingException e) {
            logger.error("Error while signing, " + ExceptionUtils.getStackTrace(e));
            return null;
        }
    }

    /**
     * Get current user by token(username_userid)
     */
    public static String getCurrentUser(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            String username = jwt.getClaim(Global.USERNAME).asString();
            String userId = jwt.getClaim(Global.USERID).asString();
            return username + "_" + userId;
        } catch (JWTDecodeException e) {
            logger.error("Error while getting current user by token, " + ExceptionUtils.getStackTrace(e));
            return null;
        }
    }
}